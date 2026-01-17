package fr.flwrian.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.flwrian.config.Config;
import fr.flwrian.config.Config.AppRoute;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Arrays;
import java.util.List;

public class ShadowHandler implements HttpHandler {

    private final Config config;
    private final HttpClient client = HttpClient.newHttpClient();

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

        byte[] bodyBytes = exchange.getRequestBody().readAllBytes();

        AppRoute route = findRoute(config.apps, host, path);
        if (route == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        try {
            System.out.println("Routing " + method + " " + path + " to " + route.prod);
            HttpRequest prodReq = buildRequest(route.prod, method, path, query, headers, bodyBytes);
            System.out.println("Forwarded request to " + prodReq.uri());
            HttpResponse<byte[]> prodResp = client.send(prodReq, HttpResponse.BodyHandlers.ofByteArray());
            System.out.println("Received response " + prodResp.statusCode() + " from " + prodReq.uri());
            exchange.getResponseHeaders().putAll(prodResp.headers().map());
            exchange.sendResponseHeaders(prodResp.statusCode(), prodResp.body().length);
            exchange.getResponseBody().write(prodResp.body());
            exchange.close();


            if (route.shadow != null && !route.shadow.isEmpty()) {
                System.out.println("Mirroring request to " + route.shadow);
                mirrorAsync(route, method, path, query, headers, bodyBytes, prodResp);
            }

        } catch (InterruptedException e) {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private AppRoute findRoute(List<AppRoute> routes, String host, String path) {
        for (AppRoute route : routes) {
            boolean hostMatches = route.match.host == null
                    || route.match.host.equalsIgnoreCase(host);
            boolean pathMatches = route.match.pathPrefix == null
                    || path.startsWith(route.match.pathPrefix);
            if (hostMatches && pathMatches) return route;
        }
        return null;
    }

    private HttpRequest buildRequest(
            String baseUrl,
            String method,
            String path,
            String query,
            Headers headers,
            byte[] bodyBytes
    ) {
        String fullUrl = baseUrl + path + (query != null ? "?" + query : "");
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .method(method,
                        bodyBytes.length == 0
                                ? HttpRequest.BodyPublishers.noBody()
                                : HttpRequest.BodyPublishers.ofByteArray(bodyBytes));

        headers.forEach((k, values) -> {
            if (!k.equalsIgnoreCase("host")
                    && !k.equalsIgnoreCase("content-length")
                    && !k.equalsIgnoreCase("connection")) {
                for (String v : values) {
                    b.header(k, v);
                }
            }
        });
        return b.build();
    }

    private void mirrorAsync(
            AppRoute route,
            String method,
            String path,
            String query,
            Headers headers,
            byte[] bodyBytes,
            HttpResponse<byte[]> prodResp
    ) {
        try {
            HttpRequest shadowReq =
                    buildRequest(route.shadow, method, path, query, headers, bodyBytes);

            client.sendAsync(shadowReq, HttpResponse.BodyHandlers.ofByteArray())
                    .thenAccept(shadowResp -> compare(prodResp, shadowResp))
                    .exceptionally(e -> {
                        System.err.println("Shadow error: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("Mirror failed: " + e.getMessage());
        }
    }

    private void compare(HttpResponse<byte[]> prod, HttpResponse<byte[]> shadow) {
        if (prod.statusCode() != shadow.statusCode()) {
            System.out.println("DIFF STATUS prod=" + prod.statusCode()
                    + " shadow=" + shadow.statusCode());
        }
        if (!Arrays.equals(prod.body(), shadow.body())) {
            System.out.println("DIFF BODY");
        }
    }
}
