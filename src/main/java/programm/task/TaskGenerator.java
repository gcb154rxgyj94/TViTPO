package programm.task;

import programm.system.TasksPlanners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Генератор задач
 */
public class TaskGenerator {

    private static final Random RANDOM = new Random();

    public static Task getNewTask(TasksPlanners scheduler) {
        return RANDOM.nextBoolean()
                ? new BasicTask()
                : new ExtendedTask(scheduler);
    }

    public static List<Task> getNewBasicTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new BasicTask());
        }
        return tasks;
    }

    public static List<Task> getNewExtendedTasks(int count, TasksPlanners scheduler) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new ExtendedTask(scheduler));
        }
        return tasks;
    }

}
