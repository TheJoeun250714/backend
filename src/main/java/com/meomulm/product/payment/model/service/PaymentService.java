package com.meomulm.product.payment.model.service;

import com.meomulm.product.payment.model.dto.*;
import com.meomulm.product.payment.model.dto.Payment;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {

    /**
     * 결제정보  추가
     */
    void postPayment(Payment payment, int reservationId, int loginUserId);

    /**
     * Stripe PaymentIntent 생성
     * @param request  금액 / 통폐화 / 예약ID
     * @return         client_secret 포함 응답
     */
    CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request);

    /**
     * Stripe 결제 확인 후 DB 저장
     * @param request  paymentIntentId + reservationId
     */
    void confirmPayment(ConfirmPaymentRequest request);
}
