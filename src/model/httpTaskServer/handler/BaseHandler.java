package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import model.manager.TaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public BaseHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void handleUnsupportedMethod(HttpExchange exchange) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD);
    }

    protected void handleInternalError(HttpExchange exchange, Exception e) throws IOException {
        sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    protected void sendResponse(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode,0);
        exchange.close();

    }
}
