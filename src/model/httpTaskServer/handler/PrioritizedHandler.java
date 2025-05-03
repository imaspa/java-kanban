package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import model.manager.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;

public class PrioritizedHandler extends BaseMethodHandle {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    void getTask(HttpExchange exchange, String[] splitPath) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }

    @Override
    void postTask(HttpExchange exchange, String[] splitPath) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD);
    }

    @Override
    void deleteTask(HttpExchange exchange, String[] splitPath) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD);
    }
}
