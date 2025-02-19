package model.task;

import model.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task{
	private List<Subtask> subtasks = new ArrayList<>();

	public Epic(Integer id, Epic task) {		super(id, task);	}

	public Epic(String name, String description) {
		super(name, description);
	}

	@Override
	public String toString() {
		return "Epic{" +
				super.toString()+
				"\t\t\tsubtasksId: %s".formatted(subtasks.stream()
						.map(subtask -> "\n\t\t\t" + subtask)
						.collect(Collectors.joining(""))) +
				"}" ;
	}

	public List<Subtask> getSubtasks() {
		return subtasks;
	}


	public void removeSubtask(Integer subtaskId) {
		subtasks.removeIf(subtask -> subtask.getId() == subtaskId);
		tuneStatus();
	}

	private Subtask findSubtask(Subtask subtask) {
		for (Subtask curent : subtasks) {
			if (curent.getId() == subtask.getId()) {
				return curent;
			}
		}
		return null;
	}

	public Epic update(Epic task) throws IllegalArgumentException{
		super.update(task);
		this.subtasks = task.getSubtasks();
		return this;
	}

	public void tuneStatus() {
		super.setTaskStatus(calcEpicTaskStatus());
	}

	private TaskStatus calcEpicTaskStatus() {
		Integer counter = 0;
		for (Subtask subTask : subtasks) {
			if (subTask.getTaskStatus()==TaskStatus.NEW) counter++;
		}
		if (counter == subtasks.size()) return TaskStatus.NEW;
		counter = 0;
		for (Subtask subTask : subtasks) {
			if (subTask.getTaskStatus()==TaskStatus.DONE) counter++;
		}
		if (counter == subtasks.size()) return TaskStatus.DONE;
		return TaskStatus.IN_PROGRESS;
	}
	public void rebuildSubtask(Subtask subtask) {
		Subtask targetSubtask = findSubtask(subtask);
		if (targetSubtask!=null) targetSubtask = subtask;
		else subtasks.add(subtask);
		tuneStatus();
	}
}
