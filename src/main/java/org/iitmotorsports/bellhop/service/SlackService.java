package org.iitmotorsports.bellhop.service;

import com.google.gson.Gson;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.http.WebhookClient;
import org.iitmotorsports.bellhop.model.Action;
import org.iitmotorsports.bellhop.model.Notification;
import org.iitmotorsports.bellhop.util.BellhopUtil;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;

@Slf4j
public class SlackService implements NotificationService {
    private static final Gson GSON = new Gson();

    public static final String MSG_SUBMITTED_FOR_APPROVAL = ":vertical_traffic_light: _%s_ has submitted *%s* for approval. The file is awaiting review and feedback from a team lead.";
    public static final String MSG_APPROVAL = ":white_check_mark: _%s_ has approved *%s*.";
    public static final String MSG_EDITING_REQUIRED = ":warning: _%s_ has requested changes for *%s*.";


    private String getNotificationMessage(Notification notification) {
        String username = notification.getUsername();
        String filename = notification.getFilename();

        if (notification.getAction() == Action.SUBMITTED_FOR_APPROVAL) {
            return String.format(MSG_SUBMITTED_FOR_APPROVAL, username, filename);
        } else if (notification.getAction() == Action.APPROVED) {
            return String.format(MSG_APPROVAL, username, filename);
        } else if (notification.getAction() == Action.EDITING_REQUIRED) {
            return String.format(MSG_EDITING_REQUIRED, username, filename);
        }

        log.error("Failed to get notification message due to an unknown state action");
        return null;
    }

    public SlackService() {
    }


    @Override
    public String getType() {
        return "slack";
    }

    public void postNotification(Notification notification, Map<String, String> config) {
        String url = config.get("url");

        if (url == null || url.isBlank()) {
            log.warn("Slack webhook URL is missing.");
            return;
        }

        log.info("Sending new action w/ action {} for {}.",
                notification.getAction(), notification.getFilename());

        String message = getNotificationMessage(notification);
        String json = GSON.toJson(Map.of("text", message));

        try {
            HttpResponse<String> response =
                    WebhookClient.getInstance().post(url, json);

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Slack notification failed: HTTP {} - {}",
                        response.statusCode(), response.body());
            } else {
                log.debug("Slack notification sent: HTTP {}",
                        response.statusCode());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Slack notification interrupted.", e);

        } catch (IOException e) {
            log.error("Failed to publish message to Slack.", e);
        }
    }
}
