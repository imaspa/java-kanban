package model.manager.inMemory;

import model.TaskType;
import model.manager.HistoryManager;
import model.manager.Managers;
import model.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static model.manager.helper.TestUtils.createTaskWithId;
import static model.manager.helper.TestUtils.updateTask;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }


    @Test
    void shouldEqualsLimit() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty());
        historyManager.add(createTaskWithId(TaskType.TASK, 1));
        historyManager.add(createTaskWithId(TaskType.TASK, 2));
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void shouldReplaceExistingTaskWhenAddingWithSameId() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty());

        Task task1 = createTaskWithId(TaskType.TASK, 1);
        historyManager.add(task1);
        Assertions.assertEquals(1, historyManager.getHistory().size());

        Task task2 = createTaskWithId(TaskType.TASK, 2);
        historyManager.add(task2);
        Assertions.assertEquals(2, historyManager.getHistory().size());

        historyManager.add(updateTask(task2, "актуальное"));
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals("актуальное", historyManager.getHistory().get(historyManager.getHistory().size() - 1).getName());
    }

    @Test
    void shouldAllowAddingAfterRemoving() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty());

        Task task1 = createTaskWithId(TaskType.TASK, 1);
        historyManager.add(task1);
        Assertions.assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(task1.getId());
        historyManager.add(createTaskWithId(TaskType.TASK, 1, "актуальное_после_удаления"));


        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals("актуальное_после_удаления", historyManager.getHistory().get(historyManager.getHistory().size() - 1).getName());
    }

    @Test
    void shouldMaintainOrderWhenReplacingTasks() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty());

        Task task1 = createTaskWithId(TaskType.TASK, 1);
        historyManager.add(task1);

        Task task2 = createTaskWithId(TaskType.TASK, 2);
        historyManager.add(task2);

        Task task3 = createTaskWithId(TaskType.TASK, 3);
        historyManager.add(task3);

        historyManager.add(updateTask(task2, "актуальная"));
        Assertions.assertEquals(3, historyManager.getHistory().size());
        var historyList = historyManager.getHistory();

        ArrayList<String> historyListTskNames = (ArrayList<String>) historyList.stream()
                .map(Task::getName)
                .collect(Collectors.toList());
        Assertions.assertArrayEquals(new String[]{"Задача_1", "Задача_3", "актуальная"}, historyListTskNames.toArray());
    }
}