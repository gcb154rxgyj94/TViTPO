package programm;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programm.system.OperatingSystem;
import programm.system.TasksPlanners;
import programm.task.Task;
import programm.task.TaskGenerator;

import java.util.List;
import java.util.Random;

public class Main {

    protected static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final int MAX_COUNT_READY_TASK = 8;
    private static final int COUNT_PRIORITY = 4;

    public static void main(String[] args) throws InterruptedException {
        int numProgramm = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        if (numProgramm < 1 || numProgramm > 3) {
            LOGGER.error("Неверный номер программы");
        } else {
            switch (numProgramm) {
                case 1: first();
                case 2: second();
                case 3: third();
            }
        }
    }

    /**
     * Только базвые задачи
     */
    public static void first() throws InterruptedException {
        TasksPlanners scheduler = new TasksPlanners(MAX_COUNT_READY_TASK, COUNT_PRIORITY);
        OperatingSystem processor = new OperatingSystem(scheduler);
        processor.setName("Processor");
        processor.start();
        List<Task> tasks = TaskGenerator.getNewBasicTasks(5);
        for (Task task : tasks) {
            processor.put(task);
            Thread.sleep(500 + new Random().nextInt(500));
        }
    }

    /**
     * Только расширенные задачи
     */
    public static void second() throws InterruptedException {
        TasksPlanners scheduler = new TasksPlanners(MAX_COUNT_READY_TASK, COUNT_PRIORITY);
        OperatingSystem processor = new OperatingSystem(scheduler);
        processor.setName("Processor");
        processor.start();
        List<Task> tasks = TaskGenerator.getNewExtendedTasks(5, scheduler);
        for (Task task : tasks) {
            processor.put(task);
            Thread.sleep(500 + new Random().nextInt(500));
        }
    }

    /**
     * С постоянным генератором задач
     */
    public static void third() throws InterruptedException {
        TasksPlanners tasksPlanners = new TasksPlanners(MAX_COUNT_READY_TASK, COUNT_PRIORITY);
        OperatingSystem operatingSystem = new OperatingSystem(tasksPlanners);
        operatingSystem.setName("Operating System");
        operatingSystem.start();
        while (true) {
            operatingSystem.put(TaskGenerator.getNewTask(tasksPlanners));
            Thread.sleep(1000 + new Random().nextInt(1000));
        }
    }

}
