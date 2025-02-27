package model.manager;

import model.manager.inMemory.InMemoryHistoryManager;
import model.manager.inMemory.InMemoryTaskManager;

public final class Managers {
	public static TaskManager getDefault() {
		return new InMemoryTaskManager();
	}

	public static HistoryManager getDefaultHistory() {
		return new InMemoryHistoryManager();
	}
}
