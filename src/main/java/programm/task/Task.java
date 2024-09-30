package programm.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Базовый класс задачи
 */
public abstract class Task implements Runnable {
    protected static final Logger LOGGER = LogManager.getLogger(Task.class);
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    protected final int id;
    protected final int priority;
    protected final Optional<Runnable> task;

    public Task() {
        this.priority = new Random().nextInt(4);
        this.id = idGenerator.getAndIncrement();
        this.task = Optional.ofNullable(getTask());
    }

    public int getPriority() {
        return priority;
    }

    public int getId() {
        return id;
    }

    protected abstract Runnable getTask();

    @Override
    public void run() {
        if (task.isEmpty()) {
            LOGGER.error("В задаче {} нет программы для выполнения", id);
            throw new IllegalArgumentException("Задача должна иметь программу для выполнения");
        }
        LOGGER.info("Задача {} начинает свое выполнение", id);
        task.get().run();
    }
}
