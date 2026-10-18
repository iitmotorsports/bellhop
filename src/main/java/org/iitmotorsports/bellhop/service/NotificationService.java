package org.iitmotorsports.bellhop.service;

import org.iitmotorsports.bellhop.model.Notification;

import java.util.Map;

public interface NotificationService {
    String getType();
    void postNotification(Notification notification, Map<String, String> config);
}
