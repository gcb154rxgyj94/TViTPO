package programm.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programm.task.ExtendedTask;
import programm.task.ReadyTasksQueue;
import programm.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Планировщик задач
 */
public class TasksPlanners {

    protected static final Logger LOGGER = LogManager.getLogger(TasksPlanners.class);
    private final List<ExtendedTask> waitingList;
    private final BlockingQueue<Task> suspendedQueue;
    private final ReadyTasksQueue readyQueue;

    public TasksPlanners(int maxCountTask, int numOfPriorities) {
        waitingList = new CopyOnWriteArrayList<>(new ArrayList<>(maxCountTask * 8));
        suspendedQueue = new ArrayBlockingQueue<>(maxCountTask * 8);
        readyQueue = new ReadyTasksQueue(maxCountTask, numOfPriorities);
        initConvertedThread();
    }

    public void putInSuspendQueue(Task task) throws InterruptedException {
        suspendedQueue.put(task);
    }

    public void putInReadyStateBlocking(Task task) {
        try {
            readyQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Задача " + task.getId() + " перешла из Suspended в Ready");
    }

    public void putInReadyStateNonBlocking(Task task) {
        try {
            readyQueue.add(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Задача " + task.getId() + " перешла из Waiting в Ready");
    }

    public Task peekFromReadyState() {
        try {
            return readyQueue.peek();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Task takeFromReadyState() {
        try {
            return readyQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void putInWaitState(ExtendedTask task) {
        LOGGER.info("Задача " + task.getId() + " перешла в состояние Waiting");
        waitingList.add(task);
    }

    private void initConvertedThread() {
        new Thread(
                () -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            putInReadyStateBlocking(suspendedQueue.take());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                },
                "Suspend -> Ready"
        ).start();
        new Thread(
                () -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        for (ExtendedTask task : waitingList) {
                            if (!task.isWaiting()) {
                                LOGGER.info("Переводим задачу " + task.getId() + " из Waiting в Ready");
                                putInReadyStateNonBlocking(task);
                                waitingList.remove(task);
                                LOGGER.info("В Waiting " + waitingList.size() + " задач");
                            }
                        }
                    }
                },
                "Waiting -> Ready"
        ).start();
    }

}

