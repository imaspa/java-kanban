package model.manager;

import model.TaskType;
import model.exception.TaskNotFound;
import model.exception.TaskValidationException;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> createOrUpdate(ArrayList<? extends Task> tasksList) throws TaskValidationException, TaskNotFound;

    Task createOrUpdate(Task task) throws TaskValidationException, TaskNotFound;

    void checkDataCreateOrUpdate(Task task) throws TaskValidationException;

    void removeAllTask(TaskType taskType);

    void removeAllTask();

    void removeTask(Integer taskId) throws TaskNotFound;

    void removeTaskById(Integer taskId) throws TaskNotFound;

    boolean isExistsTask(Integer taskId);

    List<Task> getTasks(TaskType taskType);

    Task getTaskById(Integer taskId) throws TaskNotFound;

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    Boolean isBusyTime(Task task);

}
