package model.manager.helper;

import model.TaskStatus;
import model.TaskType;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public final class TestUtils {
    private TestUtils() {
    }
    public static Task createTaskWithId(TaskType taskType, Integer id) {
        return createTaskWithId(taskType, id, null, null);
    }

    public static Task createTaskWithId(TaskType taskType, Integer id, String name) {
        return createTaskWithId(taskType, id, name, null);
    }

    public static Task createTaskWithId(TaskType taskType, Integer id, String name, Epic epic) {
        String newName = name != null ? name : "%s_%d".formatted(taskType.getName(), id);
        return switch (taskType) {
            case TASK -> new Task(id, newName, "", TaskStatus.NEW, LocalDateTime.now(), Duration.ZERO);
            case EPIC -> new Epic(id, new Task(0, newName, "", LocalDateTime.now(), Duration.ZERO));
            case SUBTASK -> new Subtask(id, newName, "", TaskStatus.NEW, epic, LocalDateTime.now(), Duration.ZERO);
        };
    }
    public static Task updateTask(Task task, String newName) {
        return switch (task.getTypeTask()) {
            case TASK ->
                    new Task(task.getId(), newName, "", task.getTaskStatus(), task.getStartTime(), task.getDuration());
            case EPIC -> new Epic(task.getId(), new Task(0, newName, "", task.getStartTime(), task.getDuration()));
            case SUBTASK ->
                    new Subtask(task.getId(), newName, "", task.getTaskStatus(), ((Subtask) task).getEpic(), task.getStartTime(), task.getDuration());
        };
    }


    public static Task createTask(TaskType taskType) {
        return createTask(taskType, null, null);
    }

    public static Task createTask(TaskType taskType,  String name) {
        return createTask(taskType,  name, null);
    }

    public static Task createTask(TaskType taskType, String name, Epic epic) {
        String newName = name != null ? name : "%s".formatted(taskType.getName());
        return switch (taskType) {
            case TASK -> new Task(newName, "", LocalDateTime.now(), Duration.ZERO);
            case EPIC -> new Epic(newName, "");
            case SUBTASK -> new Subtask(newName, "", epic, LocalDateTime.now(), Duration.ZERO);
        };
    }

}
