package org.iitmotorsports.bellhop.service;

import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.model.Action;
import org.iitmotorsports.bellhop.model.Notification;
import org.iitmotorsports.bellhop.util.BellhopUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

@Service
@Slf4j
public class SlackService {
    @Value("${bellhop.slack.webhook:}")
    private String webhookURL;

    @Autowired
    HttpClient client;

    public void postNotification(Notification notification) {
        log.info("Sending new action w/ action {} for {}.", notification.getAction(), notification.getFilename());
        String message = BellhopUtil.getNotificationMessage(notification);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookURL))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(createBodyPublisher(message))
                .build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            log.debug("Got {} back from slack.", response.statusCode());
        } catch (IOException | InterruptedException e) {
            log.error("Failed to publish message to slack.", e);
        }
    }

    private BodyPublisher createBodyPublisher(String text) {
        return BodyPublishers.ofString("{\"text\":\"" + text + "\"}");
    }
}
