package model.task;

public class Subtask extends Task {
	private Integer epicId;

	public Subtask(Integer id, Subtask task) {
		super(id, task);
		epicId = task.getEpicId();
	}
	public Subtask(String name, String description, Epic epic) {
		super(name, description);
		epicId = epic.getId();
	}

	public Integer getEpicId() {
		return epicId;
	}

	@Override
	public String toString() {
		return "Subtask{" +
				"epicId=" + epicId +
				"} " + super.toString();
	}

	public Subtask update(Subtask task) throws IllegalArgumentException{
		super.update(task);
		this.epicId = task.getEpicId();
		return this;
	}
}
