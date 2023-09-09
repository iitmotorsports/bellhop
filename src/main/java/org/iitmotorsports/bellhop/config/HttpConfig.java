package org.iitmotorsports.bellhop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.Authenticator;
import java.net.http.HttpClient;

@Configuration
public class HttpConfig {
    @Bean
    public HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }
}
