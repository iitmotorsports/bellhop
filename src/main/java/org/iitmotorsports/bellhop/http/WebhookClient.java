package org.iitmotorsports.bellhop.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebhookClient {
    private static WebhookClient INSTANCE = null;

    private final HttpClient client;

    private WebhookClient() {
        client = java.net.http.HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static WebhookClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WebhookClient();
        }
        return INSTANCE;
    }

    public HttpResponse<String> post(String url, String json)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

}
