package model.httpTaskServer;

import com.sun.net.httpserver.HttpServer;
import model.manager.HttpServ;
import model.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer implements HttpServ {

    private final HttpServer httpServer;
    private TaskManager taskManager;

    public HttpTaskServer(int port, int backlog, TaskManager taskManager) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), backlog);
            this.taskManager = taskManager;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.start();
    }

    @Override
    public void start() {
        httpServer.start();
    }

    @Override
    public void stop() {
        httpServer.stop(1);
    }
}

