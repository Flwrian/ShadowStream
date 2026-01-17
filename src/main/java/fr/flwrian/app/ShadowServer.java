package fr.flwrian.app;

import com.sun.net.httpserver.HttpServer;

import fr.flwrian.config.Config;
import fr.flwrian.handler.ShadowHandler;


import java.io.IOException;
import java.net.InetSocketAddress;


public class ShadowServer {

    Config config;

    public ShadowServer(Config config) {
        this.config = config;
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(config.server.port), 0);
            server.createContext("/", new ShadowHandler(config));
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port " + config.server.port);
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}
