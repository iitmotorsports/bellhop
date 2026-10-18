package org.iitmotorsports.bellhop.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.http.WebhookClient;
import org.iitmotorsports.bellhop.model.Action;
import org.iitmotorsports.bellhop.model.Notification;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
public class DiscordService implements NotificationService {

    private static final Gson GSON = new Gson();

    private static final int COLOR_SUBMITTED = 0xF1C40F;
    private static final int COLOR_APPROVED = 0x2ECC71;
    private static final int COLOR_EDITING_REQUIRED = 0xE74C3C;

    public DiscordService() {
    }

    @Override
    public String getType() {
        return "discord";
    }

    private String getNotificationMessage(Notification notification) {
        String username = notification.getUsername();
        String filename = notification.getFilename();

        return switch (notification.getAction()) {
            case SUBMITTED_FOR_APPROVAL ->
                    String.format(
                            "**File:** %s\n**Submitted by:** %s\n\n" +
                            "The file is awaiting review and feedback from a team lead.",
                            filename, username
                    );

            case APPROVED ->
                    String.format(
                            "**File:** %s\n**Approved by:** %s",
                            filename, username
                    );

            case EDITING_REQUIRED ->
                    String.format(
                            "**File:** %s\n**Changes requested by:** %s",
                            filename, username
                    );

            default -> null;
        };
    }

    private String getNotificationTitle(Action action) {
        return switch (action) {
            case SUBMITTED_FOR_APPROVAL -> "🚦 Submitted for Approval";
            case APPROVED -> "✅ File Approved";
            case EDITING_REQUIRED -> "⚠️ Changes Requested";
            default -> null;
        };
    }

    private int getNotificationColor(Action action) {
        return switch (action) {
            case SUBMITTED_FOR_APPROVAL -> COLOR_SUBMITTED;
            case APPROVED -> COLOR_APPROVED;
            case EDITING_REQUIRED -> COLOR_EDITING_REQUIRED;
            default -> 0;
        };
    }

    @Override
    public void postNotification(Notification notification, Map<String, String> config) {
        String url = config.get("url");

        if (url == null || url.isBlank()) {
            log.warn("Discord webhook URL is missing.");
            return;
        }

        log.info("Sending new action w/ action {} for {}.",
                notification.getAction(), notification.getFilename());

        Action action = notification.getAction();

        String title = getNotificationTitle(action);
        String description = getNotificationMessage(notification);

        if (title == null || description == null) {
            log.error("Failed to create Discord embed due to an unknown state action.");
            return;
        }

        Map<String, Object> embed = Map.of(
                "title", title,
                "description", description,
                "color", getNotificationColor(action),
                "footer", Map.of("text", "Bellhop"),
                "timestamp", Instant.now().toString()
        );

        String json = GSON.toJson(Map.of(
                "embeds", List.of(embed)
        ));

        try {
            HttpResponse<String> response =
                    WebhookClient.getInstance().post(url, json);

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Discord notification failed: HTTP {} - {}",
                        response.statusCode(), response.body());
            } else {
                log.debug("Discord notification sent: HTTP {}",
                        response.statusCode());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Discord notification interrupted.", e);

        } catch (IOException e) {
            log.error("Failed to publish message to Discord.", e);
        }
    }
}