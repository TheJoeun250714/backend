package com.meomulm.product.payment.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.notification.model.dto.Notification;
import com.meomulm.notification.model.service.NotificationService;
import com.meomulm.product.payment.model.dto.*;
import com.meomulm.product.payment.model.mapper.PaymentMapper;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.mapper.ReservationMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
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
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메세지 전송



    @Transactional
    @Override
    public void postPayment(Payment payment, int reservationId, int loginUserId) {
        if (payment == null) {
            throw new BadRequestException("결제 정보가 전달되지 않았습니다.");
        }
        Reservation isExistReservation = reservationMapper.selectReservationById(reservationId);
        if (isExistReservation == null) {
            throw new NotFoundException("예약 정보를 찾을 수 없습니다.");
        }
        payment.setReservationId(reservationId);
        paymentMapper.insertPayment(payment);
        reservationMapper.updateStatusToPaid(payment.getReservationId());

        try{
            Notification n = new Notification();
            n.setUserId(loginUserId);
            n.setNotificationContent("예약 완료! 예약 내역에서 확인해보세요.");
            n.setNotificationLinkUrl("meomulm://mypage/my-reservation?tab=0");
            notificationService.insertNotification(n);

            int generatedId = n.getNotificationId();

            // meomulm://accommodation-detail/5262

            Map<String, Object> notification = new HashMap<>();
            notification.put("id", generatedId);
            notification.put("notificationContent", "예약 완료! 예약 내역에서 확인해보세요.");
            notification.put("notificationLinkUrl", "meomulm://mypage/my-reservation?tab=0");
            notification.put("userId", loginUserId);
            notification.put("timestamp", System.currentTimeMillis());
            messagingTemplate.convertAndSendToUser(String.valueOf(loginUserId), "/queue/notifications", notification);
            log.info("String.valueOf(target.getUserId()) : {}", loginUserId);
            log.info("예약 확정 알림 전송, 저장 완료");
        } catch (Exception e) {
            log.error("예약 확정 알림 처리 실패: {}", e.getMessage());
        }
    }

    // ============================================================
    // Stripe 테스트 API 구현
    // ============================================================

    /**
     * ① PaymentIntent 생성
     *
     * Stripe 테스트 서버에 요청 → client_secret 반환
     * KRW 는 zero-decimal currency 이므로 금액을 그대로  전달
     */
    @Override
    public CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {

        // ── 입력값 검증 ──
        if (request.getAmount() <= 0) {
            throw new BadRequestException("결제 금액은 양수여야 합니다.");
        }
        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new BadRequestException("통폐화 코드가 필수입니다.");
        }

        // 예약 존재 여부 확인
        Reservation reservation = reservationMapper.selectReservationById(request.getReservationId());
        if (reservation == null) {
            throw new NotFoundException("예약 정보를 찾을 수 없습니다.");
        }

        try {
            // Stripe PaymentIntent 생성 파라미터
            Map<String, Object> params = new HashMap<>();
            params.put("amount", request.getAmount());                    // KRW: 원 단위 그대로
            params.put("currency", request.getCurrency().toLowerCase()); // "krw"
            params.put("confirm", false);                                 // Flutter SDK 에서 confirm
            params.put("payment_method_types", java.util.List.of("card"));

            // metadata 에 예약 ID 저장 → 나중에 webhook 등에서 참조 가능
            Map<String, String> metadata = new HashMap<>();
            metadata.put("reservationId", String.valueOf(request.getReservationId()));
            params.put("metadata", metadata);

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            log.info("[Stripe] PaymentIntent 생성 완료 | id={}, reservationId={}",
                    paymentIntent.getId(), request.getReservationId());

            return new CreatePaymentIntentResponse(paymentIntent.getClientSecret());

        } catch (StripeException e) {
            log.error("[Stripe] PaymentIntent 생성 실패 | code={}, msg={}",
                    e.getStripeError() != null ? e.getStripeError().getCode() : "unknown",
                    e.getMessage());
            throw new BadRequestException("Stripe PaymentIntent 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * ② Flutter SDK 결제 완료 후 서버측 확인 & DB 저장
     *
     * Stripe 로부터 PaymentIntent 를 다시 조회하여
     * status == "succeeded" 를 확인한 후에만 payment 테이블에 저장
     */
    @Transactional
    @Override
    public void confirmPayment(ConfirmPaymentRequest request) {

        // ── 입력값 검증 ──
        if (request.getPaymentIntentId() == null || request.getPaymentIntentId().isBlank()) {
            throw new BadRequestException("PaymentIntent ID 가 필수입니다.");
        }

        Reservation reservation = reservationMapper.selectReservationById(request.getReservationId());
        if (reservation == null) {
            throw new NotFoundException("예약 정보를 찾을 수 없습니다.");
        }

        try {
            // Stripe 서버에서 PaymentIntent 상태 재조회
            PaymentIntent paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());

            log.info("[Stripe] PaymentIntent 조회 | id={}, status={}",
                    paymentIntent.getId(), paymentIntent.getStatus());

            // ── 상태가 succeeded 가 아니면 거부 ──
            if (!"succeeded".equals(paymentIntent.getStatus())) {
                throw new BadRequestException(
                        "결제가 완료되지 않았습니다. Stripe 상태: " + paymentIntent.getStatus());
            }

            // ── DB 저장 (기존 insertPayment + updateStatusToPaid 활용) ──
            Payment payment = new Payment();
            payment.setReservationId(request.getReservationId());
            payment.setPaymentMethod("stripe_card");                       // 결제 수단
            payment.setPaidAmount(reservation.getTotalPrice());            // 예약의 총가격
            payment.setStatus("PAID");

            paymentMapper.insertPayment(payment);
            reservationMapper.updateStatusToPaid(request.getReservationId());

            log.info("[Stripe] 결제 확인 & DB 저장 완료 | reservationId={}, paymentIntentId={}",
                    request.getReservationId(), request.getPaymentIntentId());

        } catch (StripeException e) {
            log.error("[Stripe] PaymentIntent 조회 실패 | msg={}", e.getMessage());
            throw new BadRequestException("Stripe 결제 확인에 실패했습니다: " + e.getMessage());
        }
    }
}
