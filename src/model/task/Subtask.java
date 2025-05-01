package model.task;

import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(Integer id, String name, String description, TaskStatus taskStatus, Epic epic, LocalDateTime startTime, Duration duration) {
        super(id, name, description, taskStatus,startTime,duration);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(id, name, description,startTime,duration);
        this.epic = epic;
    }

    public Subtask(Integer id, Subtask subtask) {
        super(id, subtask);
        this.epic = subtask.getEpic();
    }

    public Subtask(String name, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, description,startTime,duration);
        this.epic = epic;
    }

    public Subtask(Task task, Epic epic) {
        super(task);
        this.epic = epic;
    }

    public Subtask(Integer id, Task task, Epic epic) {
        super(id, task);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public TaskType getTypeTask() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" + "epic=" + epic.getId() + "} " + super.toString();
    }

    @Override
    public Task create(Integer id, Task task) {
        return new Subtask(id, (Subtask) task);
    }

    @Override
    public Task update(Task task) throws IllegalArgumentException {
        super.update(task);
        this.epic = ((Subtask) task).getEpic();
        return this;
    }

    @Override
    public String taskToString(String separator) {
        return getId() + separator
                + getTypeTask().toString() + separator
                + getName() + separator
                + getDescription() + separator
                + getTaskStatus().toString() + separator
                + epic.getId() + separator
                + (getStartTime() != null ? getStartTime().toString() : "") + separator
                + (getEndTime() != null ? getEndTime().toString() : "") + separator
                +  (getDuration() != null ? getDuration().toString() : "") ;

    }
}
