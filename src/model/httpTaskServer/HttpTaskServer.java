package model.httpTaskServer;

import com.sun.net.httpserver.HttpServer;
import model.httpTaskServer.handler.BaseHandler;
import model.httpTaskServer.handler.TaskHandler;
import model.manager.HttpServ;
import model.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer  implements HttpServ {
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
            this.httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка инициализации HTTP сервера", e);
        }
    }

    @Override
    public HttpServer initServer(int port, int backlog) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), backlog);
        registerHandlers(server);
        return server;
    }

    private void registerHandlers(HttpServer server) {
        server.createContext(TASKS_PATH, new TaskHandler(taskManager));
//        server.createContext(SUBTASKS_PATH, new SubtasksHandler(taskManager));
//        server.createContext(EPICS_PATH, new EpicsHandler(taskManager));
//        server.createContext(HISTORY_PATH, new HistoryHandler(taskManager));
//        server.createContext(PRIORITIZED_PATH, new PriorityHandler(taskManager));
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