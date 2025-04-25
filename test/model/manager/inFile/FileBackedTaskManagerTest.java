package model.manager.inFile;

import model.manager.Managers;
import model.manager.TaskManager;
import model.manager.inMemory.InMemoryHistoryManagerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest extends InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private File tempFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("testFile", ".csv");
        taskManager = Managers.getFile(tempFile.getAbsolutePath());
    }

    @AfterEach
    void afterEach() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }


}
