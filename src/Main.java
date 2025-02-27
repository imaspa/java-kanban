import model.TaskStatus;
import model.TaskType;
import model.manager.HistoryManager;
import model.manager.Managers;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Main {

	public static void main(String[] args) {
		TaskManager taskManager = Managers.getDefault();
		var listTask = generateTask(taskManager, 3);
		var listTaskEpic = generateEpic(taskManager, 2);
		var listTaskSubtask = generateSubTask(taskManager, 3, listTaskEpic.get(1));
		showHead("Исходные данные");
		showAllTask(taskManager);

		taskManager.getTaskById(1);
		taskManager.getTaskById(2);
		taskManager.getTaskById(3);
		taskManager.getTaskById(4);
		taskManager.getTaskById(5);
		taskManager.getTaskById(6);
		taskManager.getTaskById(1);
		taskManager.getTaskById(7);
		taskManager.getTaskById(8);
		taskManager.getTaskById(1);
		taskManager.getTaskById(1);
		showHead("Иситория просмотрв");
		showHistory(taskManager);

		var subtask1 = listTaskSubtask.get(0);
		subtask1.setTaskStatus(TaskStatus.DONE);
		var subtask2 = listTaskSubtask.get(1);
		subtask2.setTaskStatus(TaskStatus.DONE);
		var subtask3 = listTaskSubtask.get(2);
		subtask3.setTaskStatus(TaskStatus.DONE);
		taskManager.createOrUpdate(subtask1);
		showHead("Обновление статуса в Done");
		showAllTask(taskManager);

		subtask3.setTaskStatus(TaskStatus.NEW);
		taskManager.createOrUpdate(subtask1);
		showHead("Обновление статуса EPIC в IN_PROGRESS");
		showAllTask(taskManager);

		taskManager.removeTask(8);
		showHead("Обновление статуса EPIC в DONE (удаляем Subtask)");
		showAllTask(taskManager);

		showHead("Удаляем задачу Epic (+ связанные Subtask)");
		taskManager.removeTask(5);
		showAllTask(taskManager);

//		showHead("Удалить все задачи EPIC (чтобы не было битых ссылок. удаляем и Subtask)");
//		tm.removeAllTask(TaskType.EPIC);
//		showAllTask(tm);

		showHead("Удалить все задачи TASK");
		taskManager.removeAllTask(TaskType.TASK);
		showAllTask(taskManager);

		showHead("Обработка ошибок. попытка удаления не существующего объекта");
		executeToTryCatchBlock(taskManager::createOrUpdate, new Task(500, "", ""));
	}

	public static void executeToTryCatchBlock(Consumer<Task> taskConsumer, Task task) {
		try {
			taskConsumer.accept(task);
		} catch (Exception e) {
			System.err.println("Произошла ошибка: " + e.getMessage());
		}
	}

	public static ArrayList<Task> generateTask(TaskManager tm, Integer count) {
		ArrayList<Task> tasksList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			tasksList.add(new Task("Наименование_%d".formatted(i + 1), "Описание_%d".formatted(i + 1)));
		}
		return (ArrayList<Task>) tm.createOrUpdate(tasksList);
	}

	public static ArrayList<Task> generateEpic(TaskManager tm, Integer count) {
		ArrayList<Task> tasksList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			tasksList.add(new Epic("Эпик Наименование_%d".formatted(i + 1), "Описание_%d".formatted(i + 1)));
		}
		return (ArrayList<Task>) tm.createOrUpdate(tasksList);
	}

	public static ArrayList<Task> generateSubTask(TaskManager tm, Integer count, Task epic) {
		ArrayList<Task> tasksList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			tasksList.add(new Subtask("Подзадача Наименование_%d".formatted(i + 1), "Описание_%d".formatted(i + 1), (Epic) epic));
		}
		return (ArrayList<Task>) tm.createOrUpdate(tasksList);
	}

	public static void showAllTask(TaskManager tm) {
		System.out.println("Список всех задач:");
		var tasks = tm.getTasks();
		for (TaskType taskType : tasks.keySet()) {
			System.out.println("\t%s(%s):".formatted(taskType.getName(), taskType));
			for (var task : tasks.get(taskType)) {
				System.out.println("\t\t%s".formatted(task));
			}
		}
	}

	public static void showHistory(TaskManager tm) {
		for (var current :  tm.getHistory()) {
			System.out.println("\t%s(%s)->%s (%s)".formatted(current.getTypeTask().getName(),current.getTypeTask(), current.getId(),current.getName()));
		}
	}

	public static void showHead(String title) {
		int width = 100;
		String border = "=".repeat(width);
		String centeredTitle = centerText(title, width);
		System.out.println(border + "\n" + centeredTitle + "\n" + border);
	}

	public static String centerText(String title, int width) {
		int padding = (width - title.length()) / 2;
		return " ".repeat(padding) + title + " ".repeat(padding);
	}
}