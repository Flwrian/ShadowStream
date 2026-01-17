package fr.flwrian.proxy;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import fr.flwrian.exceptions.Exceptions;
import fr.flwrian.exceptions.Exceptions.ProdTimeoutException;
import fr.flwrian.exceptions.Exceptions.ProdUnreachableException;

public class ProxyClient {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    public CompletableFuture<ProxyResponse> forward(String baseUrl, ProxyRequest req) {
        var httpReq = req.toHttpRequest(baseUrl);
        System.out.println("Forwarding request to " + baseUrl + req.path());
        return client.sendAsync(httpReq, HttpResponse.BodyHandlers.ofByteArray())
        .orTimeout(3, TimeUnit.SECONDS)
        .exceptionally(e -> {
            if (e.getCause() instanceof TimeoutException)
                throw new Exceptions().new ProdTimeoutException();
            throw new Exceptions().new ProdUnreachableException();
        })
        .whenComplete((resp, err) -> {
            if (err != null) {
                System.out.println("FORWARD ERROR: " + err.getClass() + " " + err.getMessage());
            }
        })
        .thenApply(r -> new ProxyResponse(
                r.statusCode(),
                r.headers().map(),
                r.body()
        ));

    }
}
