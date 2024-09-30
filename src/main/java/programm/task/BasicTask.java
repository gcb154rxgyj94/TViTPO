package programm.task;

import java.util.Random;

/**
 * Основная задача
 */
public class BasicTask extends Task {

    private final int RUNNING_MS = 500 + new Random().nextInt(2501);

    public BasicTask() {
        LOGGER.info("Создали BasicTask " + getId() + " с приоритетом " + getPriority());
    }

    @Override
    protected Runnable getTask() {
        return () -> {
            long nowMs = 0;
            while (nowMs < RUNNING_MS) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                nowMs += 100;
            }
            LOGGER.info("Задача {} закончилась за {} секунд", id, nowMs - 100);
        };
    }
}
