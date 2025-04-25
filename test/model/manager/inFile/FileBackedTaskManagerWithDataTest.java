package model.manager.inFile;

import model.TaskType;
import model.manager.Managers;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerWithDataTest  {
    private TaskManager taskManager;
    private File tempFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("testFile", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,TaskType,name,description,taskStatus,epic");
            writer.newLine();
            writer.write("1,TASK,задача,,NEW,");
            writer.newLine();
            writer.write("2,EPIC,эпик,,IN_PROGRESS,");
            writer.newLine();
            writer.write("3,SUBTASK,подзадача,,IN_PROGRESS,2");
        }
        taskManager = Managers.getFile(tempFile.getAbsolutePath());
    }

    @AfterEach
    void afterEach() {
        // Удаляем файл после теста
        if (tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (deleted) {
                System.out.println("Файл удален: " + tempFile.getAbsolutePath());
            } else {
                System.err.println("Не удалось удалить файл: " + tempFile.getAbsolutePath());
            }
        }
    }

    private Task createTask(TaskType taskType) {
        return createTask(taskType, null);

    }

    private Task createTask(TaskType taskType, Epic epic) {
        return switch (taskType) {
            case TASK -> new Task(taskType.getName(), "");
            case EPIC -> new Epic(taskType.getName(), "");
            case SUBTASK -> new Subtask(taskType.getName(), "", epic);
        };
    }

    @Test
    void addNewTask() {
        TaskType taskType = TaskType.TASK;
        final Task task = taskManager.createOrUpdate(createTask(taskType));
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks(taskType);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicTask() {
        TaskType taskType = TaskType.EPIC;
        final Task task = taskManager.createOrUpdate(createTask(taskType));
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks(taskType);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskTask() {
        final Task epicTask = taskManager.createOrUpdate(createTask(TaskType.EPIC));

        TaskType taskType = TaskType.SUBTASK;
        final Task task = taskManager.createOrUpdate(createTask(taskType, (Epic) epicTask));
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks(taskType);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void isLoadTask() {
        TaskType taskType = TaskType.SUBTASK;
        final List<Task> tasks = taskManager.getTasks(taskType);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }
}
