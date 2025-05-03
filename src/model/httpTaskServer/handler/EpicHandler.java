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
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;


public class EpicHandler extends BaseMethodHandle {
    private static final Pattern ALL_PATTERN = Pattern.compile("^/epics$");
    private static final Pattern BY_ID_PATTERN = Pattern.compile("^/epics/\\d+$");
    private static final Pattern EPIC_SUBTASKS_PATTERN = Pattern.compile("^/epics/\\d+/subtasks$");

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    void getTask(HttpExchange exchange, String path) throws IOException {
        boolean isAllEpics = ALL_PATTERN.matcher(path).matches();
        boolean isEpicById = BY_ID_PATTERN.matcher(path).matches();
        boolean isEpicSubtasks = EPIC_SUBTASKS_PATTERN.matcher(path).matches();

        if (true == isAllEpics) {
            handleAllEpicsRequest(exchange);
        } else if (true == isEpicById) {
            handleEpicByIdRequest(exchange, extractId(path));
        } else if (true == isEpicSubtasks) {
            handleEpicSubtasksRequest(exchange, extractId(path));
        } else {
            sendResponse(exchange, HTTP_NOT_FOUND);
        }
    }

    private void handleAllEpicsRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTasks(TaskType.EPIC)));
    }

    private void handleEpicByIdRequest(HttpExchange exchange, Integer idTask) throws IOException {
        try {
            Epic content = (Epic) taskManager.getTaskById(idTask);
            sendText(exchange, gson.toJson(content));
        } catch (TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_FOUND);
            return;
        }
    }

    private void handleEpicSubtasksRequest(HttpExchange exchange, Integer idTask) throws IOException {
        try {
            Epic content = (Epic) taskManager.getTaskById(idTask);
            sendText(exchange, gson.toJson(content.getSubtasks()));
        } catch (TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_FOUND);
            return;
        }
    }

    @Override
    void postTask(HttpExchange exchange, String path) throws IOException {
        Task task = readerFromJson(exchange);
        try {
            taskManager.createOrUpdate(task);
        } catch (TaskValidationException | TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_ACCEPTABLE);
            return;
        }
        sendResponse(exchange, HTTP_CREATED);
    }

    @Override
    void deleteTask(HttpExchange exchange, String path) throws IOException {
        Integer idTask = extractId(path);
        try {
            taskManager.removeTaskById(idTask);
        } catch (TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_FOUND);
            return;
        }
        sendResponse(exchange, HttpURLConnection.HTTP_OK);
    }

    protected Integer extractId(String path) {
        return extractId(path, 2);
    }
}
