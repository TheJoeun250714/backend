package com.meomulm.reservation.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.ForbiddenException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.common.util.ValidateUtil;
import com.meomulm.notification.model.dto.Notification;
import com.meomulm.notification.model.service.NotificationService;
import com.meomulm.product.payment.model.mapper.PaymentMapper;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.dto.ReservationDeleteRequest;
import com.meomulm.reservation.model.dto.ReservationUpdateRequest;
import com.meomulm.reservation.model.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final PaymentMapper paymentMapper;
    private final ValidateUtil validateUtil;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메세지 전송

/*    private boolean isNotExist(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String regex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(regex);
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String regex = "^010-?\\d{4}-?\\d{4}$";
        return phone.matches(regex);
    }*/

    private String changePhoneForm(String phone) {
        if (phone == null) return null;
        return phone.replace("-", "");
    }

    /**
     * 예약 추가
     * @param reservation 예약 DTO
     */
    @Transactional
    @Override
    public void postReservation(Reservation reservation) {
        if (reservation == null) {
            throw new BadRequestException("예약 정보가 전달되지 않았습니다.");
        }

        // 정규식 검증
        validateUtil.validateEmail(reservation.getBookerEmail());
        validateUtil.validateName(reservation.getBookerName());
        validateUtil.validatePhone(reservation.getBookerPhone());
        /*
        if (isNotExist(reservation.getBookerName())
                || isNotExist(reservation.getBookerEmail())
                || isNotExist(reservation.getBookerPhone())) {
            throw new BadRequestException("예약자 정보는 필수입니다.");
        }
        if (!isValidEmail(reservation.getBookerEmail())) {
            throw new BadRequestException("유효하지 않은 이메일 형식입니다.");
        }
        if (!isValidPhone(reservation.getBookerPhone())) {
            throw new BadRequestException("연락처는 010-1234-5678 또는 01012345678 형식으로 입력해주세요.");
        }
        */
        reservation.setBookerPhone(changePhoneForm(reservation.getBookerPhone()));
        reservationMapper.insertReservation(reservation);



    }

    /**
     * 예약 수정
     * @param reservation 예약 DTO
     * @param loginUserId 로그인한 유저 ID
     */
    @Transactional
    @Override
    public void patchReservation(ReservationUpdateRequest reservation, int loginUserId) {
        Reservation isExistReservation = reservationMapper.selectReservationById(reservation.getReservationId());
        if(isExistReservation == null) {
            throw new NotFoundException("수정하려는 예약을 찾을 수 없습니다.");
        }
        if(isExistReservation.getUserId() != loginUserId){
            throw new ForbiddenException("예약자 본인만 수정할 수 있습니다.");
        }
        reservationMapper.updateReservation(reservation);
    }

    /**
     * 예약 취소 (상태만 변경)
     * @param reservation 예약 DTO
     * @param loginUserId 로그인한 유저 ID
     */
    @Transactional
    @Override
    public void putReservation(ReservationDeleteRequest reservation, int loginUserId) {
        Reservation isExistReservation = reservationMapper.selectReservationById(reservation.getReservationId());
        if(isExistReservation == null) {
            throw new NotFoundException("취소하려는 예약을 찾을 수 없습니다.");
        }
        if(isExistReservation.getUserId() != loginUserId){
            throw new ForbiddenException("예약자 본인만 취소할 수 있습니다.");
        }
        reservationMapper.putReservation(reservation.getReservationId());
        paymentMapper.deletePayment(reservation.getReservationId());

        try{
            Notification n = new Notification();
            n.setUserId(isExistReservation.getUserId());
            n.setNotificationContent("예약이 정상적으로 취소 처리되었습니다.");
            n.setNotificationLinkUrl("meomulm://mypage/my-reservation?tab=2");
            notificationService.insertNotification(n);

            int generatedId = n.getNotificationId();

            Map<String, Object> notification = new HashMap<>();
            notification.put("id", generatedId);
            notification.put("userId", isExistReservation.getUserId());
            notification.put("notificationContent", "예약이 정상적으로 취소 처리되었습니다.");
            notification.put("notificationLinkUrl", "meomulm://mypage/my-reservation?tab=2");
            notification.put("timestamp", System.currentTimeMillis());
            messagingTemplate.convertAndSendToUser(String.valueOf(isExistReservation.getUserId()), "/queue/notifications", notification);
            log.info("String.valueOf(target.getUserId()) : {}", isExistReservation.getUserId());
            log.info("예약 취소 알림 전송, 저장 완료");
        } catch (Exception e) {
            log.error("예약 취소 알림 처리 실패 (ID: {}): {}", isExistReservation.getReservationId(), e.getMessage());
        }
    }


    /**
     * 예약 삭제 (미결제 종료)
     * @param reservation 예약 DTO
     * @param loginUserId 로그인한 유저 ID
     */
    @Transactional
    @Override
    public void deleteReservation(ReservationDeleteRequest reservation, int loginUserId) {
        Reservation isExistReservation = reservationMapper.selectReservationById(reservation.getReservationId());
        if(isExistReservation == null) {
            throw new NotFoundException("취소하려는 예약을 찾을 수 없습니다.");
        }
        reservationMapper.deleteReservation(reservation.getReservationId());

    }

}