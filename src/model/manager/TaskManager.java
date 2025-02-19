package model.manager;

import model.TaskType;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
	private Integer sequenceId = 0;
	public Map<TaskType, List<Task>> tasks = new HashMap<>();
	public Map<Integer, TaskType> tasksTaskTypeInd = new HashMap<>();

	public List<Task> createOrUpdate(ArrayList<? extends Task> tasksList) throws IllegalArgumentException {
		ArrayList<Task> result = new ArrayList<>();
		for (Task task : tasksList) {
			result.add(createOrUpdate(task));
		}
		return result;
	}

	public Task createOrUpdate(Task task) throws IllegalArgumentException {
		Task result;
		checkDataCreateOrUpdate(task);
		TaskType taskType = getTypeTask(task);
		if (task.getId() == null) {
			result = switch (task) {
				case Epic e -> new Epic(getGenerateSequence(), (Epic) task);
				case Subtask s -> new Subtask(getGenerateSequence(), (Subtask) task);
				case Task t -> new Task(getGenerateSequence(), task);
			};
			tasks.putIfAbsent(taskType, new ArrayList<>());
			tasks.get(taskType).add(result);
			tasksTaskTypeInd.put(result.getId(), taskType);
		} else {
			result = switch (findTaskById(task.getId())) {
				case Epic e -> e.update((Epic) task);
				case Subtask s -> s.update((Subtask) task);
				case Task t -> t.update(task);
			};
		}
		if (taskType == TaskType.SUBTASK) {
			var epicId = ((Subtask) result).getEpicId();
			var epic = (Epic) findTaskById(epicId);
			epic.rebuildSubtask((Subtask) result);
		}
		return result;
	}

	public TaskType getTypeTask(Task task) {
		return switch (task) {
			case Epic e -> TaskType.EPIC;
			case Subtask s -> TaskType.SUBTASK;
			case Task t -> TaskType.TASK;
		};
	}

	private void checkDataCreateOrUpdate(Task task) throws IllegalArgumentException {
		if (task == null) throw new IllegalArgumentException("Не передан объект задачи!");
		if (task.getId() == null) return;
		if (!isExistTask(task.getId())) throw new IllegalArgumentException("Задача по ID не найдена");
	}

	public void removeAllTask(TaskType taskType) {
		if (!tasks.containsKey(taskType)) return;
		switch (taskType) {
			case EPIC -> removeAllTask(TaskType.SUBTASK);
			case SUBTASK -> {}
			default -> {}
		}

		for (Task task : tasks.get(taskType)) {
			tasksTaskTypeInd.remove(task.getId());
		}
		tasks.remove(taskType);
	}

	public void removeAllTask() {
		tasksTaskTypeInd = new HashMap<>();
		tasks = new HashMap<>();
	}

	public void removeTask(Integer taskId) throws IllegalArgumentException {
		PatchTask patchTask = getPatchTask(taskId);
		switch (patchTask.getTaskType()) {
			case EPIC -> {
				var epic = (Epic) getTaskByPatch(patchTask);
				for (Subtask subtask : epic.getSubtasks()) {
					removeTaskById(subtask.getId());
				}
			}
			case SUBTASK -> {
				var subtask = (Subtask) getTaskByPatch(patchTask);
				var epicId = subtask.getEpicId();
				var epic = (Epic) findTaskById(epicId);
				epic.removeSubtask(subtask.getId());

			}
		}
		removeTaskById(taskId);
	}

	public void removeTaskById(Integer taskId) throws IllegalArgumentException {
		PatchTask patchTask = getPatchTask(taskId);
		tasks.get(patchTask.getTaskType()).remove(patchTask.getIndex());
		tasksTaskTypeInd.remove(taskId);
	}

	private boolean isExistTask(Integer taskId) {
		return tasksTaskTypeInd.containsKey(taskId);
	}

	private Task findTaskById(Integer taskId) throws IllegalArgumentException {
		PatchTask patchTask = getPatchTask(taskId);
		return tasks.get(patchTask.getTaskType()).get(patchTask.getIndex());
	}

	private Task getTaskByPatch(PatchTask patchTask) throws IllegalArgumentException {
		return tasks.get(patchTask.getTaskType()).get(patchTask.getIndex());
	}

	private PatchTask getPatchTask(Integer taskId) throws IllegalArgumentException {
		if (!isExistTask(taskId)) throw new IllegalArgumentException("Задача по ID не найдена");
		TaskType taskType = tasksTaskTypeInd.get(taskId);
		for (int i = 0; i < tasks.get(taskType).size(); i++) {
			var currentTaskId = tasks.get(taskType).get(i).getId();
			if (currentTaskId != taskId) continue;
			return new PatchTask(taskType, i);
		}
		throw new IllegalArgumentException("Задача по ID не найдена");
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

	private Integer getGenerateSequence() {
		return ++sequenceId;
	}


	public Map<TaskType, List<Task>> getTasks() {
		return tasks;
	}

	public Map<Integer, TaskType> getTasksTaskTypeInd() {
		return tasksTaskTypeInd;
	}
}
