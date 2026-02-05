package com.meomulm.common.scheduling;

import com.meomulm.notification.model.dto.Notification;
import com.meomulm.notification.model.service.NotificationService;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.dto.ReservationDTO;
import com.meomulm.reservation.model.mapper.ReservationMapper;
import com.meomulm.reservation.model.service.ReservationService;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.mapper.UserMapper;
import com.meomulm.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingController {

    private final NotificationService notificationService;
    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메세지 전송

    @Scheduled(cron = "0/20 * 16 * * *", zone = "Asia/Seoul")
    public void BirthDayNotification() {
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("==== 생일 자동 알림 스케줄러 시작 [{}] ====", nowTime);
        BirthdayWish();
        log.info("==== 생일 자동 알림 스케줄러 종료 ====");
    }

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    public void CheckOutNotification() {
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("==== 체크아웃 자동 알림 스케줄러 시작 [{}] ====", nowTime);
        CheckOutReview();
        log.info("==== 체크아웃 자동 알림 스케줄러 종료 ====");
    }

    @Scheduled(cron = "0 0 15 * * *", zone = "Asia/Seoul")
    public void CheckInNotification() {
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("==== 체크인 자동 알림 스케줄러 시작 [{}] ====", nowTime);
        CheckInReminder();
        log.info("==== 체크인 자동 알림 스케줄러 종료 ====");
    }

    // 오늘이 생일인 회원 조회
    private void BirthdayWish() {
        List<User> birthdayMembers = userMapper.selectTodayBirthdayList();

        for (User m : birthdayMembers) {
            try{
                Notification n = new Notification();
                n.setUserId(m.getUserId());
                n.setNotificationContent("고객님의 생일을 진심으로 축하합니다!");
                // n.setNotificationLinkUrl();
                notificationService.insertNotification(n);

                int generatedId = n.getNotificationId();

                Map<String, Object> notification = new HashMap<>();
                notification.put("id", generatedId);
                notification.put("notificationContent", "고객님의 생일을 진심으로 축하합니다!");
                notification.put("userId", m.getUserId());
                notification.put("timestamp", System.currentTimeMillis());
                messagingTemplate.convertAndSendToUser(String.valueOf(m.getUserId()), "/queue/notifications", notification);
                log.info("String.valueOf(target.getUserId()) : {}", m.getUserId());
                log.info("생일 알림 전송, 저장 완료");
            } catch (Exception e) {
                log.error("생일 알림 처리 실패 (ID: {}): {}", m.getUserId(), e.getMessage());
            }
        }
    }

    // 내일 체크인하는 예약 리스트 조회
    private void CheckInReminder() {
        List<ReservationDTO> targets = reservationMapper.selectReservationWithNames("CHECK_IN");

        for (ReservationDTO res : targets) {
            try{
                Notification n = new Notification();
                n.setUserId(res.getUserId());
                n.setNotificationContent("내일은 [" + res.getAccommodationName() + "] 체크인 날입니다!");
                n.setNotificationLinkUrl("meomulm://mypage/my-reservation?tab=0");
                notificationService.insertNotification(n);

                int generatedId = n.getNotificationId();

                Map<String, Object> notification = new HashMap<>();
                notification.put("id", generatedId);
                notification.put("userId", res.getUserId());
                notification.put("notificationContent", "내일은 [" + res.getAccommodationName() + "] 체크인 날입니다!");
                notification.put("notificationLinkUrl", "meomulm://mypage/my-reservation?tab=0");
                notification.put("timestamp", System.currentTimeMillis());
                messagingTemplate.convertAndSendToUser(String.valueOf(res.getUserId()), "/queue/notifications", notification);
                log.info("String.valueOf(target.getUserId()) : {}", res.getUserId());
                log.info("체크인 알림 전송, 저장 완료");
            } catch (Exception e) {
                log.error("체크인 알림 처리 실패 (ID: {}): {}", res.getReservationId(), e.getMessage());
            }
        }
    }

    // 오늘 체크아웃인 예약 리스트 조회
    private void CheckOutReview() {
        List<ReservationDTO> targets = reservationMapper.selectReservationWithNames("CHECK_OUT");;

        for (ReservationDTO target : targets) {
            try {
                reservationMapper.updateStatusToUsed(target.getReservationId());

                Notification n = new Notification();
                n.setUserId(target.getUserId());
                n.setNotificationContent("숙소는 어떠셨나요? [" + target.getAccommodationName() + "] 리뷰를 남겨주세요!");
                n.setNotificationLinkUrl("meomulm://mypage/my-reservation?tab=1");
                notificationService.insertNotification(n);

                int generatedId = n.getNotificationId();

                Map<String, Object> notification = new HashMap<>();
                notification.put("id", generatedId);
                notification.put("userId", target.getUserId());
                notification.put("notificationContent", "숙소는 어떠셨나요? [" + target.getAccommodationName() + "] 리뷰를 남겨주세요!");
                notification.put("notificationLinkUrl", "meomulm://mypage/my-reservation?tab=1");
                notification.put("timestamp", System.currentTimeMillis());
                messagingTemplate.convertAndSendToUser(String.valueOf(target.getUserId()), "/queue/notifications", notification);
                log.info("String.valueOf(target.getUserId()) : {}", target.getUserId());
                log.info("체크아웃 상태 변경 및 알림 전송, 저장 완료");
            } catch (Exception e) {
                log.error("체크아웃 처리 실패 (ID: {}): {}", target.getReservationId(), e.getMessage());
            }
        }
    }
}

// 링크 경로는 제대로 작성되어 있는가?
// 딥 링크 사용하려면 어떻게 수정해야 하고, 플러터에서 어떤 세팅을 해야 하는가?

// DB 저장하기 전에 개인 알림 보내기
