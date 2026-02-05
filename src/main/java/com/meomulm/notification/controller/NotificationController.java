package com.meomulm.notification.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.notification.model.dto.Notification;
import com.meomulm.notification.model.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final AuthUtil authUtil;
    private final NotificationService notificationService;

    /**
     * 회원 알림 조회
     * @param authHeader JWT 토큰 헤더
     * @return 회원 알림 리스트
     */
    @GetMapping("/list")
    public ResponseEntity<List<Notification>> getNotificationByUserId(
            @RequestHeader("Authorization") String authHeader) {

        int currentUserId = authUtil.getCurrentUserId(authHeader);
        List<Notification> notifications = notificationService.selectNotificationByUserId(currentUserId);

        return ResponseEntity.ok(notifications);
    }

    /**
     * 알림 읽음 처리 (is_read 상태 업데이트)
     * @param notificationId 알림 고유 번호
     */
    @PatchMapping("/list/{notificationId}")
    public ResponseEntity<Void> updateNotificationStatus(
//            @RequestHeader("Authorization") String authHeader,
            @PathVariable("notificationId") int notificationId) {

//        int currentUserId = authUtil.getCurrentUserId(authHeader);
        notificationService.updateNotificationStatus(notificationId);
        log.info("Notification read: notificationId={}", notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 알림 삭제
     * @param notificationId 알림 고유 번호
     */
    @DeleteMapping("/list/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
//            @RequestHeader("Authorization") String authHeader,
            @PathVariable("notificationId") int notificationId) {

//        int currentUserId = authUtil.getCurrentUserId(authHeader);
        notificationService.deleteNotification(notificationId);
        log.info("Notification deleted: notificationId={}", notificationId);
        return ResponseEntity.ok().build();
    }
}