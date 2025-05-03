package model.httpTaskServer.handler;

import com.sun.net.httpserver.HttpExchange;
import model.TaskType;
import model.exception.TaskNotFound;
import model.exception.TaskValidationException;
import model.manager.TaskManager;
import model.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;


public class TaskHandler extends BaseMethodHandle {
    private static final Pattern ALL_PATTERN = Pattern.compile("^/tasks$");
    private static final Pattern BY_ID_PATTERN = Pattern.compile("^/tasks/\\d+$");


    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    void getTask(HttpExchange exchange, String path) throws IOException {
        boolean isAllTasks = ALL_PATTERN.matcher(path).matches();
        boolean isTasksById = BY_ID_PATTERN.matcher(path).matches();

        if (true == isAllTasks) {
            handleAllRequest(exchange);
        } else if (true == isTasksById) {
            handleByIdRequest(exchange, extractId(path));
        } else {
            sendResponse(exchange, HTTP_NOT_FOUND);
        }
    }


    private void handleAllRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTasks(TaskType.TASK)));
    }

    private void handleByIdRequest(HttpExchange exchange, Integer idTask) throws IOException {
        try {
            Task content = taskManager.getTaskById(idTask);
            sendText(exchange, gson.toJson(content));
        } catch (TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_FOUND);
            return;
        }
    }

    @Override
    void postTask(HttpExchange exchange, String path) throws IOException {
        Task task = readerFromJson(exchange);
        Task createdTask = null;
        try {
            createdTask = taskManager.createOrUpdate(task);
        } catch (TaskValidationException | TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_ACCEPTABLE);
            return;
        }
        //sendResponse(exchange, HTTP_CREATED);
        sendText(exchange, gson.toJson(createdTask),HTTP_CREATED);
    }

    @Override
    void deleteTask(HttpExchange exchange, String path) throws IOException {
        Integer idTask = extractId(path);
        try {
            taskManager.removeTaskById(idTask);
        } catch (TaskNotFound e) {
            sendResponse(exchange, HTTP_NOT_FOUND);
        }
        sendResponse(exchange, HttpURLConnection.HTTP_OK);
    }

    protected Integer extractId(String path) {
        return extractId(path, 2);
    }

}
