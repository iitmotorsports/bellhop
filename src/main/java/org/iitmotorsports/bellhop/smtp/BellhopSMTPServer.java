package org.iitmotorsports.bellhop.smtp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.service.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.server.SMTPServer;

@Component
@Slf4j
public class BellhopSMTPServer {
    private SMTPServer smtpServer;

    @Autowired
    private SlackService slackService;

    @Value("${bellhop.smtp.port:25000}")
    private int port;

    @PostConstruct
    public void onStart() {
        log.info("Starting SMTP server on port {}...", port);
        smtpServer = new SMTPServer(ctx -> {
            return new SMTPHandler(ctx, slackService);
        });
        smtpServer.setSoftwareName("Bellhop");
        smtpServer.setPort(port);
        smtpServer.start();
    }

    @PreDestroy
    public void onStop() {
        log.info("Stopping SMTP server...");
        smtpServer.stop();
    }
}
