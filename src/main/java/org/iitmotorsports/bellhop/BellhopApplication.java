package org.iitmotorsports.bellhop;

import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.config.BellhopConfig;
import org.iitmotorsports.bellhop.config.ConfigManager;
import org.iitmotorsports.bellhop.http.WebhookClient;
import org.iitmotorsports.bellhop.service.NotificationDispatcher;
import org.iitmotorsports.bellhop.smtp.BellhopSMTPServer;

import java.net.http.HttpClient;

@Slf4j
public class BellhopApplication {

    public static void main(String[] args) {
        try {
            log.info("Starting Bellhop...");
            BellhopConfig config = ConfigManager.getInstance().load();
            NotificationDispatcher dispatcher =
                    new NotificationDispatcher(config);

            BellhopSMTPServer server =
                    new BellhopSMTPServer(config.smtpPort(), dispatcher);

            Runtime.getRuntime().addShutdownHook(
                    new Thread(server::stop, "bellhop-shutdown")
            );
            server.start();
            log.info("Bellhop started successfully.");
        } catch (Exception e) {
            log.error("Failed to start Bellhop.", e);
            System.exit(1);
        }
    }

}
