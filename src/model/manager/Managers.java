package model.manager;

import model.assistants.PrioritizedTasks;
import model.manager.inFile.FileBackedTaskManager;
import model.manager.inMemory.InMemoryHistoryManager;
import model.manager.inMemory.InMemoryTaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Managers {
    public static TaskManager getDefault() {
        return getFile();
    }

    public static TaskManager getMemory() {
        return new InMemoryTaskManager(getDefaultHistory(),getDefaultAsisPrioritized());
    }

    public static TaskManager getFile() {
        String fileName = "storage/taskManager.csv";
        return getFile(fileName);
    }

    public static TaskManager getFile(String fileName) {
        Path storagePatch = Paths.get(fileName == null ? "storage/taskManager.csv" : fileName).toAbsolutePath().normalize();
        String storageHead = "id,TaskType,name,description,taskStatus,epic,startTime,endTime,duration";
        Character storageSeparator = ',';
        return new FileBackedTaskManager(getDefaultHistory(),getDefaultAsisPrioritized(), storagePatch, storageHead, storageSeparator);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static PrioritizedTasks getDefaultAsisPrioritized() {
        return new PrioritizedTasks();
    }
}
