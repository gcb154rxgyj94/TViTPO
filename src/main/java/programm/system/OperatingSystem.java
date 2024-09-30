package programm.system;

import programm.task.RunningTask;
import programm.task.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Операционная система
 */
public class OperatingSystem extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(OperatingSystem.class);

    private RunningTask taskInProcess;
    private final ExecutorService executor;
    private final TasksPlanners taskPlanners;

    public OperatingSystem(TasksPlanners taskPlanners) {
        this.executor = Executors.newSingleThreadExecutor();
        this.taskPlanners = taskPlanners;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            while (true) {
                if (taskInProcess == null || taskInProcess.isCanceled() || taskInProcess.isDone()) {
                    Task task = taskPlanners.takeFromReadyState();
                    taskInProcess = new RunningTask(task, executor.submit(task));
                    break;
                } else if (taskInProcess.getTask().getPriority() < taskPlanners.peekFromReadyState().getPriority()) {
                    Task task = taskPlanners.takeFromReadyState();
                    LOGGER.info("Задача " + taskInProcess.getTask().getId() + " заменяется задачей " + task.getId());
                    taskInProcess.cancel();
                    Task prevTask = taskInProcess.getTask();
                    taskInProcess = new RunningTask(task, executor.submit(task));
                    new Thread(() -> taskPlanners.putInReadyStateBlocking(prevTask)).start();
                    break;
                }
            }
        }
    }

    public void put(Task task) throws InterruptedException {
        taskPlanners.putInSuspendQueue(task);
    }

}
