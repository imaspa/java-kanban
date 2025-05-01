package model.manager;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultTest() throws IOException {
        File tempFile = File.createTempFile("testFile", ".csv");
        TaskManager taskManager = Managers.getFile(tempFile.getAbsolutePath());
        assertNotNull(taskManager);
    }

    @Test
    void getDefaultHistoryTest() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
    }

    @Test
    void shouldReturnDifferentTaskManagerInstances() throws IOException {
        File tempFile1 = File.createTempFile("testFile1", ".csv");
        File tempFile2 = File.createTempFile("testFile2", ".csv");
        TaskManager taskManager1 = Managers.getFile(tempFile1.getAbsolutePath());
        TaskManager taskManager2 = Managers.getFile(tempFile2.getAbsolutePath());

        assertNotSame(taskManager1, taskManager2);
    }

    @Test
    void shouldReturnDifferentHistoryManagerInstances() {
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        HistoryManager historyManager2 = Managers.getDefaultHistory();

        assertNotSame(historyManager1, historyManager2);
    }
}