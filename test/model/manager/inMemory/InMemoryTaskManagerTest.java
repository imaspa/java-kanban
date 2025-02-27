package model.manager.inMemory;

import model.TaskStatus;
import model.TaskType;
import model.manager.Managers;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
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
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
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
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
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
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskTask1() {
        final Task epicTask = taskManager.createOrUpdate(createTask(TaskType.EPIC));

        TaskType taskType = TaskType.SUBTASK;
        final Task task = taskManager.createOrUpdate(createTask(taskType, (Epic) epicTask));
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks(taskType);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void historyManagerTest() {
        TaskType taskType = TaskType.TASK;
        final Task task = taskManager.createOrUpdate(createTask(taskType));
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не должна быть пустой (null)");
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Задача в истории не совпадает с добавленной");
    }

    @Test
    void updateTask() {
        TaskType taskType = TaskType.TASK;
        final Task task = taskManager.createOrUpdate(createTask(taskType));
        task.setTaskStatus(TaskStatus.DONE);
        taskManager.createOrUpdate(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(TaskStatus.DONE, savedTask.getTaskStatus(), "Статус задачи не обновился");
    }

    @Test
    void deleteTask() {
        TaskType taskType = TaskType.TASK;
        final Task task = taskManager.createOrUpdate(createTask(taskType));
        taskManager.removeTaskById(task.getId());
        assertTrue(taskManager.getTasks(taskType).isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        final Task task = taskManager.createOrUpdate(createTask(TaskType.TASK));
        final Task epic = taskManager.createOrUpdate(createTask(TaskType.EPIC));
        final Task subtask = taskManager.createOrUpdate(createTask(TaskType.EPIC, (Epic) epic));

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getTaskById(epic.getId()));
        assertEquals(subtask, taskManager.getTaskById(subtask.getId()));
    }

}