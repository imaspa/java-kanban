package model.manager;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public interface HttpServ {
    HttpServer initServer(int port, int backlog) throws IOException;

    void start();

    void stop();
}
