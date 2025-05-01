package model.task;

import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus taskStatus;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(Integer id, String name, String description, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, String name, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }

    public Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.description = task.description;
        this.taskStatus = task.taskStatus;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    public Task(Integer id, Task task) {
        this(task);
        this.id = id;
    }

    public TaskType getTypeTask() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Task create(Integer id, Task task) {
        return new Task(id, task);
    }

    public Task update(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Переданный объект не может быть null");
        }
        this.name = task.getName();
        this.description = task.getName();
        this.name = task.getName();
        this.startTime = task.startTime;
        this.duration = task.duration;
        return this;
    }

    public String taskToString(String separator) {
        return id + separator
                + getTypeTask().toString() + separator
                + name + separator
                + description + separator
                + taskStatus.toString() + separator
                + separator
                + (startTime != null ? startTime.toString() : "") + separator
                + (getEndTime() != null ? getEndTime().toString() : "") + separator
                + (duration != null ? duration.toString() : "");
    }

    @Override
    public String toString() {
        return "Task{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", taskStatus=" + taskStatus
                + ", startTime=" + (startTime != null ? startTime.toString() : null)
                + ", duration=" + (duration != null ? duration.toMinutes() + "мин." : null)
                + ", endTime=" + (getEndTime() != null ? getEndTime().toString() : null)
                + '}';
    }
}
