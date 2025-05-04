package model.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.TaskType;
import model.httpTaskServer.handler.GsonAdapters;
import model.manager.helper.HttpTestClient;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static java.net.HttpURLConnection.HTTP_OK;
import static model.manager.Managers.getHttpTaskServer;
import static model.manager.helper.TestUtils.createTask;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {

    private TaskManager taskManager;
    private HttpServ httpTaskServer;
    private final Gson gson = GsonAdapters.getGson();
    private HttpTestClient httpClient;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getMemory();
        httpTaskServer = getHttpTaskServer(taskManager);
        httpTaskServer.start();
        httpClient = new HttpTestClient();
    }

    @AfterEach
    void serverStop() {
        httpTaskServer.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        TaskType taskType = TaskType.TASK;
        final Task task = assertDoesNotThrow(() -> taskManager.createOrUpdate(createTask(taskType)));

        HttpResponse<String> response = httpClient.get("/tasks/" + task.getId());
        Task taskResponse = gson.fromJson(response.body(), Task.class);

        assertEquals(HTTP_OK, response.statusCode());
        assertNotNull(response.body());
        assertEquals(task.getName(), taskResponse.getName());
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        TaskType taskType = TaskType.TASK;
        String taskJson = gson.toJson(createTask(taskType));

        HttpResponse<String> createResponse = httpClient.post("/tasks", taskJson);
        assertEquals(HttpURLConnection.HTTP_CREATED, createResponse.statusCode());

        Task taskResponse = gson.fromJson(createResponse.body(), Task.class);
        HttpResponse<String> getResponse = httpClient.get("/tasks/" + taskResponse.getId());

        assertEquals(HTTP_OK, getResponse.statusCode());
        Task retrievedTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(taskResponse.getName(), retrievedTask.getName());
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        TaskType taskType = TaskType.TASK;
        final Task task = assertDoesNotThrow(() -> taskManager.createOrUpdate(createTask(taskType)));

        HttpResponse<String> deleteResponse = httpClient.delete("/tasks/" + task.getId());
        assertEquals(HTTP_OK, deleteResponse.statusCode());
        assertNotNull(deleteResponse.body());

        final List<Task> tasks = taskManager.getTasks(taskType);
        assertNotNull(tasks);
        assertEquals(0, tasks.size());
    }

    @Test
    void getSubtasksList() throws IOException, InterruptedException {
        final Task epicTask = assertDoesNotThrow(() -> taskManager.createOrUpdate(createTask(TaskType.EPIC)));

        TaskType taskType = TaskType.SUBTASK;
        assertDoesNotThrow(() -> {
            taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask));
            taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask));
        });

        HttpResponse<String> response = httpClient.get("/epics/%d/subtasks".formatted(epicTask.getId()));
        assertEquals(HTTP_OK, response.statusCode());
        assertNotNull(response.body());

        List<Subtask> subtasksList = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(2, subtasksList.size());
    }

    @Test
    void historyManagerTest() throws IOException, InterruptedException {
        TaskType taskType = TaskType.TASK;
        final Task task = assertDoesNotThrow(() -> taskManager.createOrUpdate(createTask(taskType)));
        final Task task1 = assertDoesNotThrow(() -> taskManager.createOrUpdate(createTask(taskType)));

        assertDoesNotThrow(() -> {
            taskManager.getTaskById(task.getId());
            taskManager.getTaskById(task1.getId());
        });

        HttpResponse<String> response = httpClient.get("/history");
        assertEquals(HTTP_OK, response.statusCode());
        assertNotNull(response.body());

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasks.size());
    }

    @Test
    void shouldPrioritizedTasks() throws IOException, InterruptedException {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        var prioritizedTasks = taskManager.getPrioritizedTasks();

        Task task1 = assertDoesNotThrow(() -> {
            Task task = createTask(TaskType.TASK);
            task.setStartTime(currentTime);
            task.setDuration(Duration.ofMinutes(50));
            return taskManager.createOrUpdate(task);
        });

        Task task2 = assertDoesNotThrow(() -> {
            Task task = createTask(TaskType.TASK);
            task.setStartTime(currentTime.plusMinutes(60));
            task.setDuration(Duration.ofMinutes(50));
            return taskManager.createOrUpdate(task);
        });

        HttpResponse<String> response = httpClient.get("/prioritized");
        assertEquals(HTTP_OK, response.statusCode());
        assertNotNull(response.body());

        Set<Task> tasks = gson.fromJson(response.body(), new TypeToken<Set<Task>>() {
        }.getType());
        assertEquals(prioritizedTasks, tasks);
    }
}