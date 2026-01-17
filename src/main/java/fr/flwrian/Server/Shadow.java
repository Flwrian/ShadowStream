package fr.flwrian.server;

import com.sun.net.httpserver.HttpServer;

import fr.flwrian.config.Config;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Shadow {

    Config config;

    public Shadow() {
        config = new Config();
        config.load();
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(config.server.port), 0);
            server.createContext("/", new RootHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port " + config.server.port);
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // handle routing
            System.out.println(exchange.getRequestURI());
        }
    }
}
