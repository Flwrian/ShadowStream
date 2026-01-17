package fr.flwrian.proxy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public record ProxyRequest(
        String method,
        String host,
        String path,
        String query,
        Headers headers,
        byte[] body
) {
    public static ProxyRequest from(HttpExchange ex) throws IOException {
        return new ProxyRequest(
                ex.getRequestMethod(),
                ex.getRequestHeaders().getFirst("Host"),
                ex.getRequestURI().getPath(),
                ex.getRequestURI().getQuery(),
                ex.getRequestHeaders(),
                ex.getRequestBody().readAllBytes()
        );
    }

    public HttpRequest toHttpRequest(String baseUrl) {
        String url = baseUrl + path + (query != null ? "?" + query : "");
        var b = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(3))
                .method(method,
                        body.length == 0
                                ? HttpRequest.BodyPublishers.noBody()
                                : HttpRequest.BodyPublishers.ofByteArray(body));

        headers.forEach((k, v) -> {
            if (!k.equalsIgnoreCase("host")
                    && !k.equalsIgnoreCase("content-length")
                    && !k.equalsIgnoreCase("connection")) {
                v.forEach(val -> b.header(k, val));
            }
        });
        return b.build();
    }
}
