package org.iitmotorsports.bellhop.service;

import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.config.BellhopConfig;
import org.iitmotorsports.bellhop.model.Notification;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class NotificationDispatcher {
    private final BellhopConfig config;
    private final Map<String, NotificationService> services;

    public NotificationDispatcher(BellhopConfig config) {
        this.config = config;

        List<NotificationService> implementations = List.of(
                new SlackService()
        );

        this.services = implementations.stream()
                .collect(Collectors.toUnmodifiableMap(
                        NotificationService::getType,
                        Function.identity()
                ));

    }

    public void dispatch(Notification notification) {
        for (var endpoint : config.endpoints()) {

            if (!endpoint.enabled()) {
                continue;
            }

            NotificationService service =
                    services.get(endpoint.type().toLowerCase());

            if (service == null) {
                log.warn("Unknown notification endpoint: {}", endpoint.type());
                continue;
            }

            try {
                service.postNotification(notification, endpoint.config());
            } catch (Exception e) {
                log.error("Unexpected error dispatching {} notification.",
                        endpoint.type(), e);
            }
        }
    }
}
