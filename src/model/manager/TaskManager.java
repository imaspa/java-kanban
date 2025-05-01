package model.manager;

import model.TaskType;
import model.exception.TaskValidationException;
import model.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> createOrUpdate(ArrayList<? extends Task> tasksList) throws TaskValidationException;

    Task createOrUpdate(Task task) throws TaskValidationException;

    void removeAllTask(TaskType taskType);

    void removeAllTask();

    void removeTask(Integer taskId) throws IllegalArgumentException;

    void removeTaskById(Integer taskId) throws IllegalArgumentException;

    boolean isExistsTask(Integer taskId);

    List<Task> getTasks(TaskType taskType);

    Task getTaskById(Integer taskId) throws IllegalArgumentException;

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    Boolean isBusyTime(Task task);

}
