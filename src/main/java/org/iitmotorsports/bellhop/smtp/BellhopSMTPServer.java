package org.iitmotorsports.bellhop.smtp;

import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.service.NotificationDispatcher;
import org.iitmotorsports.bellhop.service.SlackService;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.server.SMTPServer;

@Slf4j
public class BellhopSMTPServer {
    private final SMTPServer smtpServer;

    public BellhopSMTPServer(int smtpPort, NotificationDispatcher dispatcher) {
        SMTPHandler handler = new SMTPHandler(dispatcher);

        smtpServer = SMTPServer
                .port(smtpPort)
                .messageHandler((ctx, from, to, data) -> handler.handle(data))
                .maxMessageSize(1024*1024)
                .build();
    }

    public void start() {
        log.info("Starting Bellhop SMTP server...");
        smtpServer.start();
    }

    public void stop() {
        log.info("Stopping Bellhop SMTP server...");
        smtpServer.stop();
    }
}
