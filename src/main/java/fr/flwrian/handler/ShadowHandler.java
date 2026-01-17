package fr.flwrian.handler;

import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import fr.flwrian.config.Config;
import fr.flwrian.config.Config.AppRoute;

public class ShadowHandler implements HttpHandler{

    Config config;

    public ShadowHandler(Config config) {
        this.config = config;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String query = uri.getQuery();

        Headers headers = exchange.getRequestHeaders();
        String host = headers.getFirst("Host");

        InputStream bodyStream = exchange.getRequestBody();
        String body = new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);

        System.out.println("Method: " + method);
        System.out.println("Host: " + host);
        System.out.println("Path: " + path);
        System.out.println("Query: " + query);
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + body);

        // Find matching app route
        AppRoute route = findRoute(config.apps, host, path, method);
        if (route != null) {
            System.out.println("Matched route: " + route.name);
            // Mirror logic
        } else {
            System.out.println("No matching route found.");
        }
    }

    public AppRoute findRoute(List<AppRoute> routes, String host, String path, String method) {
        for (AppRoute route : routes) {
            boolean hostMatches = (route.match.host == null || route.match.host.equals(host));
            boolean pathMatches = (route.match.pathPrefix == null || path.startsWith(route.match.pathPrefix));
            if (hostMatches && pathMatches) {
                return route;
            }
        }
        return null;
    }
    
}
