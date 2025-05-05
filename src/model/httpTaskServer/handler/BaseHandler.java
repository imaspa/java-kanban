package model.httpTaskServer.handler;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.HttpExchange;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Subtask;
import model.task.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;

public class BaseHandler {
    protected final Gson gson;
    final TaskManager taskManager;

    public BaseHandler(TaskManager taskManager) {
        this.gson = GsonAdapters.getGson();
        this.taskManager = taskManager;
    }

    protected String[] splitPath(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        return path.split("/");
    }

    protected Integer extractId(String path, int index) {
        return extractId((path.split("/")), index);
    }

    protected Integer extractId(String[] patch, int index) {
        if ((patch.length <= index) || (!patch[index].matches("\\d+"))) {
            return null;
        }
        return Integer.parseInt(patch[index]);
    }

    protected String getKeyNode(String[] patch, int index) {
        return (patch.length <= index) ? null : patch[index];
    }

    protected void handleUnsupportedMethod(HttpExchange exchange) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD);
    }

    protected void handleInternalError(HttpExchange exchange, Exception e) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    protected void sendText(final HttpExchange exchange, final String text) throws IOException {
        sendText(exchange, text, HTTP_OK);
    }

    protected void sendText(final HttpExchange exchange, final String text, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        sendResponse(exchange, statusCode, text);
    }

    protected void sendResponse(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().close();
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (var os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected Task readerFromJson(final HttpExchange exchange) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(exchange.getRequestBody()))) {
            return switch (splitPath(exchange)[1]) {
                case "tasks" -> gson.fromJson(reader, Task.class);
                case "subtasks" -> gson.fromJson(reader, Subtask.class);
                case "epics" -> gson.fromJson(reader, Epic.class);

                default -> throw new IllegalStateException("Unexpected value: " + splitPath(exchange)[1]);
            };
        }
    }
}
