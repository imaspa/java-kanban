package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import model.TaskType;
import model.exception.TaskNotFound;
import model.exception.TaskValidationException;
import model.manager.TaskManager;
import model.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;


public class SubtaskHandler extends BaseMethodHandle {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    void getTask(HttpExchange exchange, String[] splitPath) throws IOException {
        Integer idTask = getId(splitPath, 2);
        if (idTask == null) {
            sendText(exchange, gson.toJson(taskManager.getTasks(TaskType.SUBTASK)));
        } else {
            Task content;
            try {
                content = taskManager.getTaskById(idTask);
            } catch (TaskNotFound e) {
                sendResponse(exchange, HTTP_NOT_FOUND);
                return;
            }
            sendText(exchange, gson.toJson(content));
        }

    }

    @Override
    void postTask(HttpExchange exchange, String[] splitPath) throws IOException {
        Task task = readerFromJson(exchange);
        try {
            taskManager.checkDataCreateOrUpdate(task);
        } catch (TaskValidationException e) {
            sendResponse(exchange, HTTP_NOT_ACCEPTABLE);
            return;
        }
        try {
            taskManager.createOrUpdate(task);
        } catch (TaskValidationException | TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_ACCEPTABLE);
        }
        sendResponse(exchange, HTTP_CREATED);
    }

    @Override
    void deleteTask(HttpExchange exchange, String[] splitPath) throws IOException {
        Integer idTask = getId(splitPath, 2);
        try {
            taskManager.removeTaskById(idTask);
        } catch (TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_FOUND);
        }
        sendResponse(exchange, HttpURLConnection.HTTP_OK);
    }

}
