package model.manager.inMemory;

import model.TaskStatus;
import model.TaskType;
import model.exception.TaskValidationException;
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
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения при создании/изменении задачи"
        );

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
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения при создании/изменении задачи"
        );

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
        final Task epicTask = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(TaskType.EPIC)),
                "Не ожидалось исключения при создании/изменении задачи"
        );

        TaskType taskType = TaskType.SUBTASK;
        //final Task task = taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask));
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask)),
                "Не ожидалось исключения при создании/изменении задачи"
        );
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
        final Task epicTask = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(TaskType.EPIC)),
                "Не ожидалось исключения при создании/изменении задачи"
        );

        TaskType taskType = TaskType.SUBTASK;
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask)),
                "Не ожидалось исключения при создании/изменении задачи"
        );
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
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения при создании/изменении задачи"
        );
        taskManager.getTaskById(task.getId());

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не должна быть пустой (null)");
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Задача в истории не совпадает с добавленной");
    }

    @Test
    void updateTask() {
        TaskType taskType = TaskType.TASK;
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения при создании/изменении задачи"
        );
        task.setTaskStatus(TaskStatus.DONE);
        assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения при создании/изменении задачи"
        );

        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(TaskStatus.DONE, savedTask.getTaskStatus(), "Статус задачи не обновился");
    }

    @Test
    void deleteTask() {
        TaskType taskType = TaskType.TASK;
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения при создании/изменении задачи"
        );
        taskManager.removeTaskById(task.getId());
        assertTrue(taskManager.getTasks(taskType).isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(TaskType.TASK)),
                "Не ожидалось исключения при создании/изменении задачи"
        );
        final Task epic = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(TaskType.EPIC)),
                "Не ожидалось исключения при создании/изменении задачи"
        );
        final Task subtask = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(TaskType.EPIC, null, (Epic) epic)),
                "Не ожидалось исключения при создании/изменении задачи"
        );

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getTaskById(epic.getId()));
        assertEquals(subtask, taskManager.getTaskById(subtask.getId()));
    }

    @Test
    void isBusyTime_shouldThrowWhenTimeOverlaps() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        Task task1 = createTask(TaskType.TASK);
        task1.setStartTime(currentTime);
        task1.setDuration(Duration.ofMinutes(61));

        Task task2 = createTask(TaskType.TASK);
        task2.setStartTime(currentTime.plusMinutes(60));
        task2.setDuration(Duration.ofMinutes(61));

        assertDoesNotThrow(() -> taskManager.createOrUpdate(task1));

        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> taskManager.createOrUpdate(task2),
                "Ожидалось, что пересечение задач по времени вызовет исключение"
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    void shouldPrioritizedTasks() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        var prioritizedTasks = taskManager.getPrioritizedTasks();

        Task task1 = assertDoesNotThrow(
                () -> {
                    Task task1_ = createTask(TaskType.TASK);
                    task1_.setStartTime(currentTime);
                    task1_.setDuration(Duration.ofMinutes(50));
                    return taskManager.createOrUpdate(task1_);
                },
                "Не ожидалось исключения"
        );

        Task task2 = assertDoesNotThrow(
                () -> {
                    Task task2_ = createTask(TaskType.TASK);
                    task2_.setStartTime(currentTime.plusMinutes(60));
                    task2_.setDuration(Duration.ofMinutes(50));
                    return taskManager.createOrUpdate(task2_);
                },
                "Не ожидалось исключения"
        );


        assertEquals(2, prioritizedTasks.size());
        Iterator<Task> iterator = prioritizedTasks.iterator();
        assertEquals(task1, iterator.next());
        assertEquals(task2, iterator.next());
        assertFalse(iterator.hasNext());


        Task task3 = assertDoesNotThrow(
                () -> {
                    Task task3_ = createTask(TaskType.TASK);  // создаём внутри лямбды
                    task3_.setStartTime(currentTime.minusMinutes(60));
                    task3_.setDuration(Duration.ofMinutes(50));
                    return taskManager.createOrUpdate(task3_);
                },
                "Не ожидалось исключения"
        );

        assertEquals(3, prioritizedTasks.size());
        iterator = prioritizedTasks.iterator();
        assertEquals(task3, iterator.next());
        assertEquals(task1, iterator.next());
        assertEquals(task2, iterator.next());
        assertFalse(iterator.hasNext());
    }

}