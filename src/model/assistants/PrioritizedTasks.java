package model.assistants;


import model.task.Task;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class PrioritizedTasks {
    private final Set<Task> tasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public Set<Task> getTasks() {
        return tasks;
    }

    public void addOrUpdateTask(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        if (tasks.contains(task)) {
            removeTask(task);
        }
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public void removeAll() {
        tasks.clear();
    }
}
