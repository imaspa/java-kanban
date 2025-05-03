package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import model.TaskType;
import model.exception.TaskNotFound;
import model.exception.TaskValidationException;
import model.manager.TaskManager;
import model.task.Epic;
import model.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;


public class EpicHandler extends BaseMethodHandle {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    void getTask(HttpExchange exchange, String[] splitPath) throws IOException {
        Epic content = null;
        Integer idTask = getId(splitPath, 2);
        String keyNode = getKeyNode(splitPath, 3);
        if (idTask != null) {
            try {
                content = (Epic) taskManager.getTaskById(idTask);
            } catch (TaskNotFound e) {
                sendResponse(exchange, HTTP_NOT_FOUND);
                return;
            }
        }

        if (keyNode == null && idTask == null) {
            sendText(exchange, gson.toJson(taskManager.getTasks(TaskType.EPIC)));
        } else if (keyNode != null) {
            if (content != null && content.getSubtasks().isEmpty()) {
                sendResponse(exchange, HTTP_NOT_FOUND);
            } else {
                sendText(exchange, gson.toJson(content.getSubtasks()));
            }
        } else {
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
