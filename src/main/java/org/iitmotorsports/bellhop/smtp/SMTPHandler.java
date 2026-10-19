package org.iitmotorsports.bellhop.smtp;

import com.google.gson.*;
import jakarta.mail.BodyPart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.model.Action;
import org.iitmotorsports.bellhop.model.Notification;
import org.iitmotorsports.bellhop.service.NotificationDispatcher;
import org.iitmotorsports.bellhop.service.SlackService;
import org.iitmotorsports.bellhop.util.BellhopUtil;

import java.io.ByteArrayInputStream;
import java.util.Properties;


@Slf4j
public class SMTPHandler {

    private static final Gson GSON = new GsonBuilder().create();

    private final NotificationDispatcher dispatcher;

    public SMTPHandler(NotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void handle(byte[] data) {
        try {
            log.info("Incoming email!");
            Session session = Session.getInstance(new Properties());

            MimeMessage message = new MimeMessage(
                    session,
                    new ByteArrayInputStream(data)
            );

            if (!(message.getContent() instanceof MimeMultipart multipart)) {
                log.error("Expected a multipart MIME message.");
                return;
            }

            if (multipart.getCount() < 2) {
                log.error("Invalid email format: missing JSON body.");
                return;
            }

            BodyPart body = multipart.getBodyPart(1);

            if (!(body.getContent() instanceof String content)) {
                log.error("Expected a text MIME body.");
                return;
            }

            // Preserve PDM's existing leading-character workaround.
            if (content.isEmpty()) {
                log.error("Empty notification body.");
                return;
            }

            String json = content.substring(1);

            JsonArray array = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();

                if (obj.isEmpty()) {
                    log.debug("Ignoring trailing empty JSON object.");
                    continue;
                }

                String username = obj.get("user").getAsString();
                String source = obj.get("source").getAsString();
                String dest = obj.get("dest").getAsString();
                String filename = obj.get("file").getAsString();
                String description = obj.get("description").getAsString();

                Action action = BellhopUtil.getActionFromSourceAndDest(
                        source, dest
                );

                if (action.isInvalid()) {
                    log.error(
                            "Invalid transition: {} -> {}",
                            source, dest
                    );
                    continue;
                }

                Notification notification =
                        new Notification(username, filename, action, description);

                dispatcher.dispatch(notification);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing incoming message", e);
        }

    }
}
