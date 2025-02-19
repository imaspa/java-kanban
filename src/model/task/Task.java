package model.task;

import model.TaskStatus;
import model.TaskType;

import java.util.Objects;

public class Task {
	private Integer id;
	private String name;
	private String description;
	private TaskStatus taskStatus;

	public Task(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.taskStatus = TaskStatus.NEW;
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
		if (this == o) {return true;}
		if (o == null || getClass() != o.getClass()) {return false;}
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

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Task create(Integer id, Task task) {
		return new Task(id, task);
	}

	public Task update(Task task) {
		if (task == null) {throw new IllegalArgumentException("Переданный объект не может быть null");}
		this.name = task.getName();
		this.description = task.getName();
		this.name = task.getName();
		return this;
	}

	@Override
	public String toString() {
		return "Task{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", taskStatus=" + taskStatus +
				'}';
	}
}
