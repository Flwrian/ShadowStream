package fr.flwrian.handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import fr.flwrian.config.Config;
import fr.flwrian.mirror.MirrorService;
import fr.flwrian.proxy.ProxyClient;
import fr.flwrian.proxy.ProxyRequest;
import fr.flwrian.proxy.ProxyResponse;
import fr.flwrian.proxy.Router;

/**
 * Main HTTP handler.
 * - Receives incoming requests
 * - Routes them to the correct backend (prod)
 * - Forwards the response to the client
 * - Optionally mirrors the request to a shadow backend
 */
public class ShadowHandler implements HttpHandler {

    private final Router router;
    private final ProxyClient proxy;
    private final MirrorService mirror;


    public ShadowHandler(Config config) {
        this.router = new Router(config.apps);
        this.proxy = new ProxyClient();
        this.mirror = new MirrorService(proxy);
    }

    /**
     * Entry point for each incoming HTTP request.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Convert raw HttpExchange into a clean domain object
        var req = ProxyRequest.from(exchange);

        // Find matching route (host + path prefix)
        var route = router.match(req);
        if (route == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        // Forward request to production backend
        proxy.forward(route.prod, req)
            .orTimeout(3, TimeUnit.SECONDS)
            .whenComplete((resp, err) -> {
                try {
                    if (err != null) {
                        System.out.println("Prod failed: " + err);
                        send(exchange, 504, null);
                    } else {
                        send(exchange, resp);
                    }
                } catch (Exception e) {
                    try {
                        exchange.close();
                    } catch (Exception ignored) {}
                }
            });

    }

    /**
     * Sends a full response back to the client.
     */
    private void send(HttpExchange ex, ProxyResponse resp) {
        try {
            ex.getResponseHeaders().putAll(resp.headers());
            ex.sendResponseHeaders(resp.status(), resp.body().length);
            ex.getResponseBody().write(resp.body());
            ex.close();
        } catch (IOException ignored) {
            // Client probably disconnected
        }
    }

    /**
     * Sends only a status code, with optional body.
     */
    private void send(HttpExchange ex, int status, byte[] body) {
        try {
            ex.sendResponseHeaders(status, body == null ? -1 : body.length);
            if (body != null) ex.getResponseBody().write(body);
            ex.close();
        } catch (IOException ignored) {
            // Best-effort response
        }
    }
}
