package model.manager.inMemory;

import model.TaskStatus;
import model.TaskType;
import model.exception.TaskBusyTimeException;
import model.manager.Managers;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;

import static model.manager.helper.TestUtils.createTask;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getMemory();
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
        final Task task = taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask));
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
        final Task task = taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask));
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
        final Task subtask = taskManager.createOrUpdate(createTask(TaskType.EPIC, null,(Epic) epic));

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getTaskById(epic.getId()));
        assertEquals(subtask, taskManager.getTaskById(subtask.getId()));
    }

    @Test
    void isBusyTime_shouldThrowTaskBusyTimeExceptionWhenTimeOverlaps() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        Task task1 = createTask(TaskType.TASK);
        task1.setStartTime(currentTime);
        task1.setDuration(Duration.ofMinutes(61));

        Task task2 = createTask(TaskType.TASK);
        task2.setStartTime(currentTime.plusMinutes(60));
        task2.setDuration(Duration.ofMinutes(61));

        assertDoesNotThrow(() -> taskManager.createOrUpdate(task1));

        TaskBusyTimeException exception = assertThrows(
                TaskBusyTimeException.class,
                () -> taskManager.createOrUpdate(task2),
                "Ожидалось, что пересечение задач по времени вызовет исключение"
        );
       assertNotNull(exception.getMessage());
    }

    @Test
    void shouldPrioritizedTasks() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        var prioritizedTasks = taskManager.getPrioritizedTasks();

        Task task1 = createTask(TaskType.TASK);
        task1.setStartTime(currentTime);
        task1.setDuration(Duration.ofMinutes(50));

        Task task2 = createTask(TaskType.TASK);
        task2.setStartTime(currentTime.plusMinutes(60));
        task2.setDuration(Duration.ofMinutes(50));

        task1 = taskManager.createOrUpdate(task1);
        task2 = taskManager.createOrUpdate(task2);

        assertEquals(2, prioritizedTasks.size());
        Iterator<Task> iterator = prioritizedTasks.iterator();
        assertEquals(task1, iterator.next());
        assertEquals(task2, iterator.next());
        assertFalse(iterator.hasNext());

        Task task3 = createTask(TaskType.TASK);
        task3.setStartTime(currentTime.minusMinutes(60));
        task3.setDuration(Duration.ofMinutes(50));
        task3 = taskManager.createOrUpdate(task3);

        assertEquals(3, prioritizedTasks.size());
        iterator = prioritizedTasks.iterator();
        assertEquals(task3, iterator.next());
        assertEquals(task1, iterator.next());
        assertEquals(task2, iterator.next());
        assertFalse(iterator.hasNext());


    }

}