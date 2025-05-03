package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.manager.TaskManager;

import java.io.IOException;

public abstract class BaseMethodHandle extends BaseHandler implements HttpHandler {
    public BaseMethodHandle(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] splitPath = splitPath(exchange);
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> getTask(exchange, splitPath);
                case "POST" -> postTask(exchange, splitPath);
                case "DELETE" -> deleteTask(exchange, splitPath);
                default -> handleUnsupportedMethod(exchange);
            }
        } catch (Exception e) {
            handleInternalError(exchange, e);
        }
    }


    abstract void getTask(HttpExchange exchange, String[] splitPath) throws IOException;

    abstract void postTask(HttpExchange exchange, String[] splitPath) throws IOException;

    abstract void deleteTask(HttpExchange exchange, String[] splitPath) throws IOException;

}
