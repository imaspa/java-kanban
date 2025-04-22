package model.manager.inMemory;

import model.manager.HistoryManager;
import model.manager.Managers;
import model.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;


    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    private Task createTask(Integer id) {
        return new Task(id, "Наименование_%d".formatted(id), "Описание_%d".formatted(id));
    }

    private Task createTask(Integer id, String text) {
        return new Task(id, "Наименование_%s_%d".formatted(text, id), "Описание_%s_%d".formatted(text, id));
    }

    @Test
    void shouldEqualsLimit() {
        for (int i = 0; i < 30; i++) {
            historyManager.add(createTask(i));
        }
        Assertions.assertEquals(30, historyManager.getHistory().size());
    }

    @Test
    void shouldReplaceExistingTaskWhenAddingWithSameId() {
        historyManager.add(createTask(1));
        Assertions.assertEquals(1, historyManager.getHistory().size());

        historyManager.add(createTask(2));
        Assertions.assertEquals(2, historyManager.getHistory().size());

        historyManager.add(createTask(1, "актуальное"));
        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertEquals("Наименование_актуальное_1", historyManager.getHistory().get(historyManager.getHistory().size() - 1).getName());
    }

    @Test
    void shouldAllowAddingAfterRemoving() {
        historyManager.add(createTask(1));
        historyManager.add(createTask(1, "актуальное"));
        Assertions.assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(1);
        historyManager.add(createTask(1, "актуальное_после_удаления"));

        Assertions.assertEquals(1, historyManager.getHistory().size());
        Assertions.assertEquals("Наименование_актуальное_после_удаления_1", historyManager.getHistory().get(historyManager.getHistory().size() - 1).getName());
    }


    @Test
    void shouldMaintainOrderWhenReplacingTasks() {
        historyManager.add(createTask(1));
        historyManager.add(createTask(2));
        historyManager.add(createTask(3));

        historyManager.add(createTask(2, "актуальная"));
        Assertions.assertEquals(3, historyManager.getHistory().size());
        var historyList = historyManager.getHistory();

        ArrayList<String> historyListTskNames = (ArrayList<String>) historyList.stream().map(Task::getName).collect(Collectors.toList());
        Assertions.assertArrayEquals(new String[]{"Наименование_1", "Наименование_3", "Наименование_актуальная_2"}, historyListTskNames.toArray());
    }

}