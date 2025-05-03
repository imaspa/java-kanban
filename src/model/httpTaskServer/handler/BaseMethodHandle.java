package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.exception.TaskNotFound;
import model.manager.TaskManager;

import java.io.IOException;
import java.net.URI;

public abstract class BaseMethodHandle extends BaseHandler implements HttpHandler {
    public BaseMethodHandle(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //String[] splitPath = splitPath(exchange);
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> getTask(exchange, path);
                case "POST" -> postTask(exchange, path);
                case "DELETE" -> deleteTask(exchange, path);
                default -> handleUnsupportedMethod(exchange);
            }
        } catch (Exception e) {
            handleInternalError(exchange, e);
        }
    }


    abstract void getTask(HttpExchange exchange, String path) throws IOException, TaskNotFound;

    abstract void postTask(HttpExchange exchange, String path) throws IOException;

    abstract void deleteTask(HttpExchange exchange, String path) throws IOException;

}
