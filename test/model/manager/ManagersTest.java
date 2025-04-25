package model.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultTest() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
    }

    @Test
    void getDefaultHistoryTest() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
    }

    @Test
    void shouldReturnDifferentTaskManagerInstances() {
        TaskManager taskManager1 = Managers.getDefault();
        TaskManager taskManager2 = Managers.getDefault();

        assertNotSame(taskManager1, taskManager2);
    }

    @Test
    void shouldReturnDifferentHistoryManagerInstances() {
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        HistoryManager historyManager2 = Managers.getDefaultHistory();

        assertNotSame(historyManager1, historyManager2);
    }
}