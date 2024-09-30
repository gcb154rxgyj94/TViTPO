package programm.task;

import java.util.concurrent.Future;

/**
 * Прямо сейчас запущенная задача
 */
public class RunningTask {

    private final Task task;
    private final Future<?> future;

    public RunningTask(Task task, Future<?> future) {
        this.task = task;
        this.future = future;
    }

    public Task getTask() {
        return task;
    }

    public void cancel() {
        future.cancel(true);
    }

    public boolean isDone() {
        return future.isDone();
    }

    public boolean isCanceled() {
        return future.isCancelled();
    }

}
