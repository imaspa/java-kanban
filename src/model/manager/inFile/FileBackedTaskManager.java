package model.manager.inFile;

import model.TaskStatus;
import model.TaskType;
import model.assistants.PrioritizedTasks;
import model.exception.StorageException;
import model.exception.TaskBusyTimeException;
import model.manager.HistoryManager;
import model.manager.TaskManager;
import model.manager.inMemory.InMemoryTaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static model.utils.FileUtils.createOrAppendCSVFile;
import static model.utils.FileUtils.readCSVFileAsStream;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path storagePatch;
    private final String storageHead;
    private final Character storageSeparator;

    public FileBackedTaskManager(HistoryManager historyManager, PrioritizedTasks prioritizedTasks, Path patchStorage, String patchStorageHead, Character patchStorageSeparator) {
        super(historyManager,prioritizedTasks);
        this.storagePatch = patchStorage;
        this.storageHead = patchStorageHead;
        this.storageSeparator = patchStorageSeparator;
        loadFromFile();
    }

    public Character getStorageSeparator() {
        return storageSeparator;
    }

    public String getStorageHead() {
        return storageHead;
    }

    public Path getStoragePatch() {
        return storagePatch;
    }

    private void loadFromFile() {
        List<LineCsvDto> fileData = readFile();
        processCsvData(fileData);
    }

    private List<LineCsvDto> readFile() {
        try (Stream<String> lines = readCSVFileAsStream(this.getStoragePatch(), this.getStorageHead())) {
            return lines
                    .skip(1)
                    .filter(line -> !line.trim().isEmpty())
                    .map(entity -> new LineCsvDto(entity, this.getStorageSeparator()))
                    .toList();
        } catch (IOException e) {
            throw new StorageException("Ошибка при чтении файла: " + this.getStoragePatch(), e);
        }
    }

    private void processCsvData(List<LineCsvDto> fileData) {
        fileData.stream()
                .filter(entity -> ("TASK".equals(entity.taskType) || "EPIC".equals(entity.taskType)))
                .forEach(this::parseTaskFromCsv);

        fileData.stream()
                .filter(entity -> ("SUBTASK".equals(entity.taskType)))
                .forEach(this::parseTaskFromCsv);

        int maxId = fileData.stream()
                .mapToInt(entity -> Integer.parseInt(entity.id))
                .max()
                .orElse(0);
        this.sequenceId = maxId;
    }

    static class LineCsvDto {
        String id;
        String taskType;
        String name;
        String description;
        String taskStatus;
        String epic;
        String startTime;
        String endTime;
        String duration;

        public LineCsvDto(String line, Character separator) {
            String[] parts = line.split(separator.toString(),-1);
            this.id = parts[0];
            this.taskType = parts[1];
            this.name = parts[2];
            this.description = parts[3];
            this.taskStatus = parts[4];
            this.epic = parts[5];
            this.startTime = parts[6];
            this.endTime = parts[7];
            this.duration =  parts[8];//parts.length < 9 ? null : parts[8];
        }
    }

    private void parseTaskFromCsv(LineCsvDto data) {
        Integer id = Integer.valueOf(data.id);
        String name = data.name;
        String description = data.description;
        TaskType taskType = TaskType.valueOf(data.taskType);
        TaskStatus taskStatus = TaskStatus.valueOf(data.taskStatus);
        LocalDateTime startTime = (data.startTime == null || data.startTime.trim().isEmpty()) ? null : LocalDateTime.parse(data.startTime);
        Duration duration = (data.duration == null || data.duration.trim().isEmpty()) ? null : Duration.parse(data.duration);

        switch (taskType) {
            case TASK -> createTask(new Task(id, name, description, taskStatus, startTime, duration));
            case EPIC -> createTask(new Epic(id, new Task(id, name, description, taskStatus, startTime, duration)));
            case SUBTASK ->
                    createTask(new Subtask(id, name, description, taskStatus, (Epic) findTaskById(Integer.valueOf(data.epic)), startTime, duration));
        }
    }

    private void createTask(Task taskIn) throws IllegalArgumentException, TaskBusyTimeException {
        isBusyTime(taskIn);
        tasks.putIfAbsent(taskIn.getTypeTask(), new ArrayList<>());
        prioritizedTasks.addOrUpdateTask(taskIn);
        tasks.get(taskIn.getTypeTask()).add(taskIn);
        tasksTaskTypeInd.put(taskIn.getId(), taskIn.getTypeTask());
        if (taskIn.getTypeTask() == TaskType.SUBTASK) {
            ((Subtask) taskIn).getEpic().rebuildSubtask((Subtask) taskIn);
        }
    }

    @Override
    public Task createOrUpdate(Task task) throws IllegalArgumentException {
        var result = super.createOrUpdate(task);
        saveToCsvFile(result);
        return result;
    }

    private void saveToCsvFile(Task task) {
        var line = task.taskToString(storageSeparator.toString());
        try {
            createOrAppendCSVFile(storagePatch, storageHead, line);
        } catch (IOException e) {
            throw new StorageException("Ошибка при чтении файла: " + this.getStoragePatch(), e);
        }
    }
}
