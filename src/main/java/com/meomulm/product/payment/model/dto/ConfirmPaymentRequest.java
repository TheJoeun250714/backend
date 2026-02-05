package com.meomulm.product.payment.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /api/payment/stripe/confirm 요청 본문
 *
 * paymentIntentId – Stripe 에서 확인된 PaymentIntent 의 ID (pi_xxxxx)
 * reservationId   – 결제가 완료된 예약 ID
 */
@Data
@NoArgsConstructor
public class ConfirmPaymentRequest {
    private String paymentIntentId;
    private int reservationId;
}