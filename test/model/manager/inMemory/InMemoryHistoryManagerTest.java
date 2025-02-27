package model.manager.inMemory;

import model.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
	private InMemoryHistoryManager historyManager;


	@BeforeEach
	void setUp() {
		historyManager = new InMemoryHistoryManager();
	}

	private Task createTask(Integer id) {
		return new Task(id, "Наименование_%d".formatted(id), "Описание_ %d".formatted(id));
	}

	@Test
	void shouldEqualsLimit() {
		for (int i = 0; i < historyManager.HISTORY_LIMIT + 2; i++) {
			historyManager.add(createTask(i));
		}
		Assertions.assertEquals(historyManager.getHistory().size(), historyManager.HISTORY_LIMIT);
	}
}