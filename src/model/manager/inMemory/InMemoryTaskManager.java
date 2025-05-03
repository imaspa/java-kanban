package model.manager.inMemory;

import model.TaskType;
import model.assistants.PrioritizedTasks;
import model.exception.TaskNotFound;
import model.exception.TaskValidationException;
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

    protected final PrioritizedTasks prioritizedTasks;
    private final HistoryManager history;
    protected Integer sequenceId;
    protected Map<TaskType, List<Task>> tasks = new HashMap<>();
    protected Map<Integer, TaskType> tasksTaskTypeInd = new HashMap<>();

    public InMemoryTaskManager(HistoryManager historyManager, PrioritizedTasks prioritizedTasks) {
        sequenceId = 0;
        history = historyManager;
        this.prioritizedTasks = prioritizedTasks;
    }

    @Override
    public List<Task> createOrUpdate(ArrayList<? extends Task> tasksList) throws TaskValidationException, TaskNotFound {

        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasksList) {
            result.add(createOrUpdate(task));
        }
        return result;
    }


    @Override
    public Task createOrUpdate(Task task) throws TaskValidationException, TaskNotFound {
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

    @Override
    public void checkDataCreateOrUpdate(Task task) throws TaskValidationException {
        if (task == null) {
            throw new TaskValidationException("Не передан объект задачи!");
        }
        if (isBusyTime(task)) {
            throw new TaskValidationException("Время занято");
        }
        if (task.getId() == null) {
            return;
        }
        if (!isExistsTask(task.getId())) {
            throw new TaskValidationException("Задача по ID не найдена");
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
    public void removeTask(Integer taskId) throws TaskNotFound {
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
    public void removeTaskById(Integer taskId) throws TaskNotFound {
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

    protected Task findTaskById(Integer taskId) throws TaskNotFound {
        PatchTask patchTask = getPatchTask(taskId);
        return tasks.get(patchTask.getTaskType()).get(patchTask.getIndex());
    }

    private PatchTask getPatchTask(Integer taskId) throws TaskNotFound {
        if (!isExistsTask(taskId)) {
            throw new TaskNotFound("Задача по ID не найдена");
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
        throw new TaskNotFound("Задача по ID не найдена");
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
    public Task getTaskById(Integer taskId) throws TaskNotFound {
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks.getTasks();
    }

    @Override
    public Boolean isBusyTime(Task taskIn) {
        LocalDateTime newStart = taskIn.getStartTime();
        LocalDateTime newEnd = taskIn.getEndTime();
        if (newStart == null || newEnd == null) {
            return false;
        }
        boolean isBusy = prioritizedTasks.getTasks().stream()
                .filter(task -> !task.equals(taskIn))
                .anyMatch(task ->
                        task.getStartTime().isBefore(newEnd) &&
                                task.getEndTime().isAfter(newStart)
                );
        return isBusy;
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

}