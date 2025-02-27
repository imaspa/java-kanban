package model.task;

import model.TaskType;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(Integer id, Subtask subtask) {
        super(id, subtask);
        this.epic = subtask.getEpic();
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
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
}
