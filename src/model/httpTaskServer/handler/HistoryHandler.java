package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import model.manager.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;

public class HistoryHandler extends BaseMethodHandle {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    void getTask(HttpExchange exchange, String path) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getHistory()));
    }

    @Override
    void postTask(HttpExchange exchange, String path) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD);
    }

    @Override
    void deleteTask(HttpExchange exchange, String path) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD);
    }
}
