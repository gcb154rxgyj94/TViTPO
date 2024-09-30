package programm.task;

import programm.system.TasksPlanners;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Расширенная задача
 */
public class ExtendedTask extends Task {

    private final TasksPlanners tasksPlanners;
    private final int RUNNING_MS = 500 + new Random().nextInt(2501);
    private final AtomicBoolean isWaiting = new AtomicBoolean(false);
    private int nowMs = 0;
    private long waitingMs;

    public ExtendedTask(TasksPlanners tasksPlanners) {
        this.tasksPlanners = tasksPlanners;
        LOGGER.info("Создали ExtendedTask " + getId() + " с приоритетом " + getPriority());
    }

    public boolean isWaiting() {
        return isWaiting.get();
    }

    @Override
    protected Runnable getTask() {
        return () -> {
            if (nowMs > 0) {
                LOGGER.info("Продолжаем с " + nowMs + " миллисекунд");
            }
            while (nowMs < RUNNING_MS) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (new Random().nextInt(101) > 99) {
                    isWaiting.set(true);
                    waitingMs = 1000 + new Random().nextInt(1001);
                    LOGGER.info("Задача " + id + " ушла в ожидание на " + nowMs + " миллисекундах на " + waitingMs + " миллисекунд");
                    tasksPlanners.putInWaitState(this);
                    new Thread(() -> {
                        try {
                            Thread.sleep(waitingMs);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        isWaiting.set(false);
                    }).start();
                    return;
                }
                nowMs += 100;
            }
            LOGGER.info("Задача {} закончилась за {} миллисекунд", id, nowMs - 100);
        };
    }

}
