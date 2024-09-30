package programm.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Очередь Ready-Задач
 */
public class ReadyTasksQueue {

    protected static final Logger LOGGER = LogManager.getLogger(ReadyTasksQueue.class);

    private final List<BlockingQueue<Task>> queues = new ArrayList<>();
    private final int maxCountTaskInQueue;
    private int generalCountTasks = 0;

    public ReadyTasksQueue(int maxCountTask, int countPriorities) {
        this.maxCountTaskInQueue = maxCountTask;
        for (int i = 0; i < countPriorities; i++) {
            queues.add(new LinkedBlockingQueue<>(maxCountTaskInQueue + 1));
        }
    }

    public synchronized Task take() throws InterruptedException {
        LOGGER.info("Берем из ready очереди задачу");
        while (generalCountTasks == 0) {
            wait();
        }
        ListIterator<BlockingQueue<Task>> iterator = queues.listIterator(queues.size());
        while (iterator.hasPrevious()) {
            BlockingQueue<Task> tasks = iterator.previous();
            if (!tasks.isEmpty()) {
                generalCountTasks--;
                Task task = tasks.take();
                LOGGER.info("Взяли задачу " + task.getId() + ". Теперь в очереди ready " + generalCountTasks + " задач");
                notifyAll();
                return task;
            }
        }
        notifyAll();
        return null;
    }

    public synchronized void put(Task task) throws InterruptedException {
        LOGGER.info("put: Пытается положить в ready очередь задачу " + task.getId());
        while (generalCountTasks >= maxCountTaskInQueue) {
            LOGGER.info("put: Ждем пока уменьшиться количество задач");
            wait();
        }
        queues.get(task.getPriority()).put(task);
        generalCountTasks++;
        notifyAll();
        LOGGER.info("Положили в ready очередь задачу " + task.getId() + ". Теперь в ready очереди " + generalCountTasks + " задач");
    }

    public synchronized void add(Task task) throws InterruptedException {
        LOGGER.info("add: Пытаемся положить в ready очередь задачу " + task.getId());
        queues.get(task.getPriority()).put(task);
        generalCountTasks++;
        notifyAll();
        LOGGER.info("Положили в ready очередь задачу " + task.getId() + ". Теперь в ready очереди " + generalCountTasks + " задач");
    }

    public synchronized Task peek() throws InterruptedException {
        while (generalCountTasks == 0) {
            wait();
        }
        ListIterator<BlockingQueue<Task>> iterator = queues.listIterator(queues.size());
        while (iterator.hasPrevious()) {
            BlockingQueue<Task> tasks = iterator.previous();
            if (!tasks.isEmpty()) {
                return tasks.peek();
            }
        }
        return null;
    }

}
