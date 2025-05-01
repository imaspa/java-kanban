package model.manager.inMemory;

import model.TaskType;
import model.assistants.PrioritizedTasks;
import model.exception.TaskBusyTimeException;
import model.manager.HistoryManager;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager history;
    protected final PrioritizedTasks prioritizedTasks;

    protected Integer sequenceId;
    protected Map<TaskType, List<Task>> tasks = new HashMap<>();
    protected Map<Integer, TaskType> tasksTaskTypeInd = new HashMap<>();

    public InMemoryTaskManager(HistoryManager historyManager, PrioritizedTasks prioritizedTasks) {
        sequenceId = 0;
        history = historyManager;
        this.prioritizedTasks = prioritizedTasks;
    }

    @Override
    public List<Task> createOrUpdate(ArrayList<? extends Task> tasksList) throws IllegalArgumentException {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasksList) {
            result.add(createOrUpdate(task));
        }
        return result;
    }

    @Override
    public Task createOrUpdate(Task task) throws IllegalArgumentException,TaskBusyTimeException {
        Task result;
        checkDataCreateOrUpdate(task);
        if (task.getId() == null) {
            result = task.create(getGenerateSequence(), task);

            tasks.putIfAbsent(result.getTypeTask(), new ArrayList<>());
            prioritizedTasks.addOrUpdateTask(result);
            tasks.get(result.getTypeTask()).add(result);
            tasksTaskTypeInd.put(result.getId(), result.getTypeTask());
        } else {
            result = findTaskById(task.getId()).update(task);
        }
        if (result.getTypeTask() == TaskType.SUBTASK) {
            ((Subtask) result).getEpic().rebuildSubtask((Subtask) result);
        }
        return result;
    }

    protected void checkDataCreateOrUpdate(Task task) throws IllegalArgumentException, TaskBusyTimeException {
        if (task == null) {
            throw new IllegalArgumentException("Не передан объект задачи!");
        }
        isBusyTime(task);
        if (task.getId() == null) {
            return;
        }
        if (!isExistsTask(task.getId())) {
            throw new IllegalArgumentException("Задача по ID не найдена");
        }
    }

    @Override
    public void removeAllTask(TaskType taskType) {
        if (!tasks.containsKey(taskType)) {
            return;
        }
        if (taskType == TaskType.EPIC) {
            removeAllTask(TaskType.SUBTASK);
        }
        for (Task task : tasks.get(taskType)) {
            tasksTaskTypeInd.remove(task.getId());
            prioritizedTasks.removeTask(task);
        }
        tasks.remove(taskType);
    }

    @Override
    public void removeAllTask() {
        tasksTaskTypeInd = new HashMap<>();
        tasks = new HashMap<>();
        prioritizedTasks.removeAll();
    }


    @Override
    public void removeTask(Integer taskId) throws IllegalArgumentException {
        switch (findTaskById(taskId)) {
            case Epic e -> {
                for (Subtask subtask : e.getSubtasks()) {
                    removeTaskById(subtask.getId());
                }
            }
            case Subtask s -> s.getEpic().removeSubtask(s.getId());
            default -> {
            }
        }
        removeTaskById(taskId);
    }

    @Override
    public void removeTaskById(Integer taskId) throws IllegalArgumentException {
        PatchTask patchTask = getPatchTask(taskId);
        prioritizedTasks.removeTask(findTaskById(taskId));
        tasks.get(patchTask.getTaskType()).remove(patchTask.getIndex());
        tasksTaskTypeInd.remove(taskId);
        history.remove(taskId);
    }

    @Override
    public boolean isExistsTask(Integer taskId) {
        return tasksTaskTypeInd.containsKey(taskId);
    }

    protected Task findTaskById(Integer taskId) throws IllegalArgumentException {
        PatchTask patchTask = getPatchTask(taskId);
        return tasks.get(patchTask.getTaskType()).get(patchTask.getIndex());
    }

    private PatchTask getPatchTask(Integer taskId) throws IllegalArgumentException {
        if (!isExistsTask(taskId)) {
            throw new IllegalArgumentException("Задача по ID не найдена");
        }
        TaskType taskType = tasksTaskTypeInd.get(taskId);
        var taskChunk = tasks.get(taskType);
        for (int i = 0; i < taskChunk.size(); i++) {
            var currentTaskId = taskChunk.get(i).getId();
            if (!currentTaskId.equals(taskId)) {
                continue;
            }
            return new PatchTask(taskType, i);
        }
        throw new IllegalArgumentException("Задача по ID не найдена");
    }

    private void addHistory(Task task) {
        history.add(task);
    }

    private Integer getGenerateSequence() {
        return ++sequenceId;
    }

    private Map<TaskType, List<Task>> getTasks() {
        return tasks;
    }

    @Override
    public List<Task> getTasks(TaskType taskType) {
        if (!getTasks().containsKey(taskType)) return new ArrayList<>();
        return getTasks().get(taskType);
    }

    @Override
    public Task getTaskById(Integer taskId) throws IllegalArgumentException {
        Task task = findTaskById(taskId);
        addHistory(task);
        return task;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }


    public Map<Integer, TaskType> getTasksTaskTypeInd() {
        return tasksTaskTypeInd;
    }

    public static class PatchTask {
        public TaskType taskType;
        public int index;

        public PatchTask(TaskType taskType, Integer index) {
            this.taskType = taskType;
            this.index = index;
        }

        public TaskType getTaskType() {
            return taskType;
        }

        public int getIndex() {
            return index;
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks.getTasks();
    }

    @Override
    public void isBusyTime(Task taskIn) throws TaskBusyTimeException {
        LocalDateTime newStart = taskIn.getStartTime();
        LocalDateTime newEnd = taskIn.getEndTime();
        if (newStart == null || newEnd == null) {
            return;
        }
        boolean isBusy = prioritizedTasks.getTasks().stream()
                .filter(task -> !task.equals(taskIn))
                .anyMatch(task ->
                        task.getStartTime().isBefore(newEnd) &&
                                task.getEndTime().isAfter(newStart)
                );

        if (isBusy) {
            throw new TaskBusyTimeException("Время занято задачей (тип/id/имя/время):%s/%d/%s/%s - %s".formatted(taskIn.getTypeTask().toString(), taskIn.getId(), taskIn.getName(), taskIn.getStartTime().toString(), taskIn.getEndTime().toString()));
        }
    }

}