package model.task;

import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(Integer id, Task task) {
        super(id, task);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        var subtaskStr = "\n\t\t\tsubtasksId: %s".formatted(subtasks.stream()
                .map(subtask -> "\n\t\t\t" + subtask)
                .collect(Collectors.joining("")));
        return "Epic{" + super.toString() + subtaskStr + "}";
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Integer subtaskId) {
        subtasks.removeIf(subtask -> subtask.getId().equals(subtaskId));
        tuneTask();

    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void tuneTask() {
        setTaskStatus(calcEpicTaskStatus());
        setStartTime(calcEpicTaskStartTime());
        setEndTime(calcEpicTaskEndTime());
        setDuration(calcEpicTaskDuration());
    }

    private Duration calcEpicTaskDuration() {
        if (Objects.isNull(getStartTime()) || Objects.isNull(getEndTime())) {
            return null;
        }
        return Duration.between(getStartTime(), getEndTime());
    }

    private LocalDateTime calcEpicTaskStartTime() {
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime calcEpicTaskEndTime() {
        return subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
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
        tuneTask();
    }
}
