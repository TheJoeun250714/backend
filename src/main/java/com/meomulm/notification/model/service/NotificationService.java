package com.meomulm.notification.model.service;

import com.meomulm.notification.model.dto.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> selectNotificationByUserId(int currentUserId);
    void insertNotification(Notification notification);
    void updateNotificationStatus(int notificationId);
    void deleteNotification(int notificationId);
}
