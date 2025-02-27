package model.manager;

import model.TaskType;
import model.task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> createOrUpdate(ArrayList<? extends Task> tasksList) throws IllegalArgumentException;

    Task createOrUpdate(Task task) throws IllegalArgumentException;

    void removeAllTask(TaskType taskType);

    void removeAllTask();

    void removeTask(Integer taskId) throws IllegalArgumentException;

    void removeTaskById(Integer taskId) throws IllegalArgumentException;

    boolean isExistsTask(Integer taskId);

    List<Task> getTasks(TaskType taskType);

    Task getTaskById(Integer taskId) throws IllegalArgumentException;

    List<Task> getHistory();

}
