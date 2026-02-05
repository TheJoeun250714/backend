package com.meomulm.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequestMapping
@RestController
@RequiredArgsConstructor
public class WebSocketNotificationController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 클라이언트가 /app/notify 로 메세지를 보내면
     * /topic/notifications 를 구독한 모든 클라이언트에게 브로드캐스트
     * 브로드캐스트란 한 개의 송신자가 네트워크 내 모든 장치에게 데이터를 동시에 전송하는 방식이다.
     * @param msg
     * @return
     */
    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Map<String, Object> sendNotification(Map<String, Object> msg) {
        log.info("알림 메세지 수신 및 브로드캐스트 : {}", msg);
        return msg;
    }

    /**
     * 특정 사용자에게만 알림 전송한다.
     * @param userId       사용자 이름
     * @param notification   알림 내용
     */
    public void sendToUser(int userId, Map<String, Object> notification) {
        log.info("사용자 {}에게 개인 알림 전송 : {}", userId, notification);
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notifications", notification);
    }

    /**
     * 모든 사용자에게 알림 전송한다.
     * @param notification   알림 내용
     */
    public void broadcastNotification(Map<String, Object> notification) {
        log.info("모든 사용자에게 알림 브로드캐스트 : {}", notification);
        simpMessagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}