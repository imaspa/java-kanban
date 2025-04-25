package model.task;

import model.TaskStatus;
import model.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(Integer id, Task task) {
        super(id, task);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        var subtaskStr = "\t\t\tsubtasksId: %s".formatted(subtasks.stream()
                .map(subtask -> "\n\t\t\t" + subtask)
                .collect(Collectors.joining("")));
        return "Epic{" + super.toString() + subtaskStr + "}";
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Integer subtaskId) {
        subtasks.removeIf(subtask -> subtask.getId().equals(subtaskId));
        tuneStatus();
    }

    private Subtask findSubtask(Subtask subtask) {
        return subtasks.stream()
                .filter(current -> current.getId().equals(subtask.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public TaskType getTypeTask() {
        return TaskType.EPIC;
    }

    public void tuneStatus() {
        super.setTaskStatus(calcEpicTaskStatus());
    }

    private TaskStatus calcEpicTaskStatus() {
        if (subtasks.isEmpty()) return TaskStatus.NEW;
        Set<TaskStatus> taskStatusSet = subtasks.stream()
                .map(Subtask::getTaskStatus)
                .collect(Collectors.toSet());
        return (taskStatusSet.size() == 1) ? taskStatusSet.iterator().next() : TaskStatus.IN_PROGRESS;
    }

    @Override
    public Task create(Integer id, Task task) {
        return new Epic(id, (Epic) task);
    }

    @Override
    public Task update(Task task) throws IllegalArgumentException {
        super.update(task);
        this.subtasks = ((Epic) task).getSubtasks();
        return this;
    }

    public void rebuildSubtask(Subtask subtask) {
        Subtask targetSubtask = findSubtask(subtask);
        if (targetSubtask != null) {
            targetSubtask.update(subtask);
        } else {
            subtasks.add(subtask);
        }
        tuneStatus();
    }
}
