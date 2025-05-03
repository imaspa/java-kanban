package model.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.TaskType;
import model.httpTaskServer.handler.GsonAdapters;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static model.manager.Managers.getHttpTaskServer;
import static model.manager.helper.TestUtils.createTask;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {

    private Gson gson;
    private TaskManager taskManager;
    private HttpServ HttpTaskServer;


    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getMemory();
        HttpTaskServer = getHttpTaskServer(taskManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new GsonAdapters.DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new GsonAdapters.LocalDateTimeAdapter())
                .registerTypeAdapter(Subtask.class, new GsonAdapters.SubtaskAdapter())
                .registerTypeAdapter(Epic.class, new GsonAdapters.EpicAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        HttpTaskServer.start();
    }

    @AfterEach
    void serverStop() {
        HttpTaskServer.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        TaskType taskType = TaskType.TASK;
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения"
        );

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task taskResponse = gson.fromJson(response.body(), Task.class);

            assertEquals(HTTP_OK, response.statusCode(), "Неверный статус ответа");
            assertNotNull(response.body(), "Body не получено");
            assertEquals(task.getName(), taskResponse.getName(), "Имя задачи отличается");
        }
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        TaskType taskType = TaskType.TASK;
        String taskJson = gson.toJson(createTask(taskType));

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode(), "Неверный статус ответа");

            Task taskResponse = gson.fromJson(response.body(), Task.class);

            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/" + taskResponse.getId()))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HTTP_OK, response.statusCode(), "Неверный статус ответа");

            Task taskResponse1 = gson.fromJson(response.body(), Task.class);
            assertEquals(taskResponse.getName(), taskResponse1.getName(), "Некорректное имя");
        }
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        TaskType taskType = TaskType.TASK;
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType)),
                "Не ожидалось исключения"
        );

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                    .DELETE()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HTTP_OK, response.statusCode(), "Неверный статус ответа");
            assertNotNull(response.body(), "Body не получено");

            final List<Task> tasks = taskManager.getTasks(taskType);

            assertNotNull(tasks, "Задачи не возвращаются.");
            assertEquals(0, tasks.size(), "Неверное количество задач.");
        }
    }

    @Test
    void getSubtasksList() throws IOException, InterruptedException {
        final Task epicTask = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(TaskType.EPIC)),
                "Не ожидалось исключения"
        );

        TaskType taskType = TaskType.SUBTASK;
        final Task task = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask)),
                "Не ожидалось исключения"
        );
        final Task task1 = assertDoesNotThrow(
                () -> taskManager.createOrUpdate(createTask(taskType, null, (Epic) epicTask)),
                "Не ожидалось исключения"
        );

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics/%d/subtasks".formatted(epicTask.getId())))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HTTP_OK, response.statusCode(), "Неверный статус ответа");
            assertNotNull(response.body(), "Body не получено");

            List<Subtask> subtasksList = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
            }.getType());


        }


//            List<Subtask> subtasksList = gson.fromJson(getResponse.body(), new TypeToken<List<Subtask>>() {
//            }.getType());
//
//            assertEquals(manager.getSubtasksFromEpic(manager.getEpicById(1)), subtasksList, "Некорректный список");

}
}
