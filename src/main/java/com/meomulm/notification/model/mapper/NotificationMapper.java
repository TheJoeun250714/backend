package com.meomulm.notification.model.mapper;

import com.meomulm.notification.model.dto.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    List<Notification> selectNotificationByUserId(int userId);
    int insertNotification(Notification notification);
    int updateNotificationStatus(int notificationId);
    int deleteNotification(int notificationId);
}
