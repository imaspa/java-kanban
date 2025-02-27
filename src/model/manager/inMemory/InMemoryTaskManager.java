package model.manager.inMemory;

import model.TaskType;
import model.manager.HistoryManager;
import model.manager.Managers;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

	private Integer sequenceId;
	private Map<TaskType, List<Task>> tasks = new HashMap<>();
	private Map<Integer, TaskType> tasksTaskTypeInd = new HashMap<>();
	private final HistoryManager history;

	public InMemoryTaskManager() {
		sequenceId = 0;
		history = Managers.getDefaultHistory();
	}

	@Override
	public List<Task> createOrUpdate(ArrayList<? extends Task> tasksList) throws IllegalArgumentException {
		ArrayList<Task> result = new ArrayList<>();
		for (Task task : tasksList) {
			result.add(createOrUpdate(task));
		}
		return result;
	}

	@Override
	public Task createOrUpdate(Task task) throws IllegalArgumentException {
		Task result;
		checkDataCreateOrUpdate(task);
		if (task.getId() == null) {
			result = task.create(getGenerateSequence(), task);
			tasks.putIfAbsent(result.getTypeTask(), new ArrayList<>());
			tasks.get(result.getTypeTask()).add(result);
			tasksTaskTypeInd.put(result.getId(), result.getTypeTask());
		} else {
			result = findTaskById(task.getId()).update(task);
		}
		if (result.getTypeTask() == TaskType.SUBTASK) {
			((Subtask) result).getEpic().rebuildSubtask((Subtask) result);
		}
		return result;
	}

	private void checkDataCreateOrUpdate(Task task) throws IllegalArgumentException {
		if (task == null) {throw new IllegalArgumentException("Не передан объект задачи!");}
		if (task.getId() == null) {return;}
		if (!isExistsTask(task.getId())) {throw new IllegalArgumentException("Задача по ID не найдена");}
	}

	@Override
	public void removeAllTask(TaskType taskType) {
		if (!tasks.containsKey(taskType)) {return;}
		if (taskType == TaskType.EPIC) {removeAllTask(TaskType.SUBTASK);}
		for (Task task : tasks.get(taskType)) {
			tasksTaskTypeInd.remove(task.getId());
		}
		tasks.remove(taskType);
	}

	@Override
	public void removeAllTask() {
		tasksTaskTypeInd = new HashMap<>();
		tasks = new HashMap<>();
	}

	@Override
	public void removeTask(Integer taskId) throws IllegalArgumentException {
		switch (findTaskById(taskId)) {
			case Epic e -> {
				for (Subtask subtask : e.getSubtasks()) {
					removeTaskById(subtask.getId());
				}
			}
			case Subtask s -> s.getEpic().removeSubtask(s.getId());
			default -> {}
		}
		removeTaskById(taskId);
	}

	@Override
	public void removeTaskById(Integer taskId) throws IllegalArgumentException {
		PatchTask patchTask = getPatchTask(taskId);
		tasks.get(patchTask.getTaskType()).remove(patchTask.getIndex());
		tasksTaskTypeInd.remove(taskId);
	}

	@Override
	public boolean isExistsTask(Integer taskId) {
		return tasksTaskTypeInd.containsKey(taskId);
	}

	private Task findTaskById(Integer taskId) throws IllegalArgumentException {
		PatchTask patchTask = getPatchTask(taskId);
		return tasks.get(patchTask.getTaskType()).get(patchTask.getIndex());
	}

	private PatchTask getPatchTask(Integer taskId) throws IllegalArgumentException {
		if (!isExistsTask(taskId)) {throw new IllegalArgumentException("Задача по ID не найдена");}
		TaskType taskType = tasksTaskTypeInd.get(taskId);
		for (int i = 0; i < tasks.get(taskType).size(); i++) {
			var currentTaskId = tasks.get(taskType).get(i).getId();
			if (currentTaskId != taskId) {continue;}
			return new PatchTask(taskType, i);
		}
		throw new IllegalArgumentException("Задача по ID не найдена");
	}

	private void addHistory(Task task) {
		history.add(task);
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

	@Override
	public Map<TaskType, List<Task>> getTasks() {
		return tasks;
	}

	@Override
	public List<Task> getTasks(TaskType taskType) {
		return getTasks().get(taskType);
	}

	@Override
	public Task getTaskById(Integer taskId) throws IllegalArgumentException {
		Task task = findTaskById(taskId);
		addHistory(task);
		return task;
	}

	@Override
	public List<Task> getHistory() {
		return history.getHistory();
	}

	public Map<Integer, TaskType> getTasksTaskTypeInd() {
		return tasksTaskTypeInd;
	}
}