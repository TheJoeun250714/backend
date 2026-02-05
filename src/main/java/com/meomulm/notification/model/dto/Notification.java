package com.meomulm.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private int notificationId;
    private int userId;
    private String notificationContent;
    private String notificationLinkUrl;
    private boolean isRead;
    private String createdAt;

}
