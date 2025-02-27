package model.manager.inMemory;

import model.manager.HistoryManager;
import model.manager.Managers;
import model.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;


    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    private Task createTask(Integer id) {
        return new Task(id, "Наименование_%d".formatted(id), "Описание_ %d".formatted(id));
    }

    @Test
    void shouldEqualsLimit() {
        for (int i = 0; i < 11; i++) {
            historyManager.add(createTask(i));
        }
        Assertions.assertEquals(historyManager.getHistory().size(), 10);
    }
}