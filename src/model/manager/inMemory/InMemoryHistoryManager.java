package model.manager.inMemory;

import model.manager.HistoryManager;
import model.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final Integer HISTORY_LIMIT = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= HISTORY_LIMIT) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
