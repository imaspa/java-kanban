package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.manager.TaskManager;

import java.io.IOException;


public class TaskHandler extends BaseMethodHandle{

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    void getTask(HttpExchange httpExchange, String path) throws IOException {

    }

    @Override
    void postTask(HttpExchange httpExchange, String path) throws IOException {

    }

    @Override
    void deleteTask(HttpExchange httpExchange, String path) throws IOException {

    }

}
