package org.iitmotorsports.bellhop.smtp;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.iitmotorsports.bellhop.model.Action;
import org.iitmotorsports.bellhop.model.Notification;
import org.iitmotorsports.bellhop.service.SlackService;
import org.iitmotorsports.bellhop.util.BellhopUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;

import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


@Slf4j
public class SMTPHandler implements MessageHandler {

    private static final Gson GSON = new GsonBuilder().create();
    private byte[] data = {};

    private SlackService slackService;

    public SMTPHandler(MessageContext ctx, SlackService slackService) {
        this.slackService = slackService;
    }

    @Override
    public void from(String from) {
    }

    @Override
    public void recipient(String recipient) {
    }

    @Override
    public void data(InputStream is) throws IOException {
        data = IOUtils.toByteArray(is);
    }

    @Override
    public void done() {
        try {
            Session s = Session.getDefaultInstance(new Properties());
            MimeMessage mimeMsg = new MimeMessage(s, new ByteArrayInputStream(data));
            MimeMultipart multipart = (MimeMultipart) mimeMsg.getContent();
            if (multipart.getCount() < 2) {
                log.error("Invalid email format. Please confirm the sender and try again.");
                return;
            }
            BodyPart bp = multipart.getBodyPart(1);
            String messageContent = (String) bp.getContent();
            messageContent = messageContent.substring(1);
            JsonArray array = GSON.fromJson(messageContent, JsonArray.class);
            for (JsonElement e : array) {
                JsonObject obj = e.getAsJsonObject();
                if (obj.isEmpty()) {
                    log.debug("Ignoring final tag. Continuing processing.");
                    continue;
                }

                String username = obj.get("user").getAsString();
                String source = obj.get("source").getAsString();
                String dest = obj.get("dest").getAsString();
                String filename = obj.get("file").getAsString();
                Action action = BellhopUtil.getActionFromSourceAndDest(source, dest);

                if (action.isInvalid()) {
                    log.error("Transition action is invalid. Source: {}, Dest: {}", source, dest);
                    continue;
                }

                Notification notification = new Notification(username, filename, action);
                slackService.postNotification(notification);
            }
        } catch (Exception e) {
            log.error("Error occurred while processing incoming message", e);
        }

    }
}
