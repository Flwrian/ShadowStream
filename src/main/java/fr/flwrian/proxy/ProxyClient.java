package fr.flwrian.proxy;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProxyClient {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    public CompletableFuture<ProxyResponse> forward(String baseUrl, ProxyRequest req) {
        var httpReq = req.toHttpRequest(baseUrl);

        return client.sendAsync(httpReq, HttpResponse.BodyHandlers.ofByteArray())
                .orTimeout(3, TimeUnit.SECONDS)
                .thenApply(r -> new ProxyResponse(
                        r.statusCode(),
                        r.headers().map(),
                        r.body()
                ));
    }
}
