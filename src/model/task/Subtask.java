package model.task;

import model.TaskStatus;
import model.TaskType;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(Integer id, String name, String description, TaskStatus taskStatus, Epic epic) {
        super(id, name, description, taskStatus);
        this.epic = epic;
    }

    public Subtask(Integer id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
    }

    public Subtask(Integer id, Subtask subtask) {
        super(id, subtask);
        this.epic = subtask.getEpic();
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
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
                + epic.getId();
    }
}
