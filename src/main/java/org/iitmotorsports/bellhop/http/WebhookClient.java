package org.iitmotorsports.bellhop.http;

import java.net.http.HttpClient;
import java.time.Duration;

public class HttpClientFactory {
    private HttpClientFactory() {}

    public static HttpClient create() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
}
