package model.httpTaskServer;

import com.sun.net.httpserver.HttpServer;
import model.httpTaskServer.handler.EpicHandler;
import model.httpTaskServer.handler.HistoryHandler;
import model.httpTaskServer.handler.PrioritizedHandler;
import model.httpTaskServer.handler.SubtaskHandler;
import model.httpTaskServer.handler.TaskHandler;
import model.manager.HttpServ;
import model.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class HttpTaskServer implements HttpServ {
    private static final String TASKS_PATH = "/tasks";
    private static final String SUBTASKS_PATH = "/subtasks";
    private static final String EPICS_PATH = "/epics";
    private static final String HISTORY_PATH = "/history";
    private static final String PRIORITIZED_PATH = "/prioritized";
    private static final int STOP_DELAY_SECONDS = 1;

    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(int port, int backlog, TaskManager taskManager) {
        this.taskManager = taskManager;
        try {
            this.httpServer = initServer(port, backlog);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка инициализации HTTP сервера", e);
        }
    }

    @Override
    public HttpServer initServer(int port, int backlog) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), backlog);
        registerHandlers(server);
        server.createContext("/", exchange -> {
            if (!isSupportedPath(exchange.getRequestURI().getPath())) {
                exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                exchange.getResponseBody().close();
            }
        });
        return server;
    }

    private boolean isSupportedPath(String path) {
        return path.equals(TASKS_PATH) ||
                path.equals(SUBTASKS_PATH) ||
                path.equals(EPICS_PATH) ||
                path.equals(HISTORY_PATH) ||
                path.equals(PRIORITIZED_PATH);
    }

    private void registerHandlers(HttpServer server) {
        server.createContext(TASKS_PATH, new TaskHandler(taskManager));
        server.createContext(SUBTASKS_PATH, new SubtaskHandler(taskManager));
        server.createContext(EPICS_PATH, new EpicHandler(taskManager));
        server.createContext(HISTORY_PATH, new HistoryHandler(taskManager));
        server.createContext(PRIORITIZED_PATH, new PrioritizedHandler(taskManager));
    }

    @Override
    public void start() {
        if (httpServer != null) {
            httpServer.start();
        }
    }

    @Override
    public void stop() {
        if (httpServer != null) {
            httpServer.stop(STOP_DELAY_SECONDS);
        }
    }
}