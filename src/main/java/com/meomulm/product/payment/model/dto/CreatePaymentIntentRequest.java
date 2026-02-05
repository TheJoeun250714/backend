package com.meomulm.product.payment.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POST /api/payment/stripe/create-payment-intent 요청 본문
 *
 * amount          – 결제 금액 (원 단위, 정수)
 * currency        – 통폐화 코드 (예: "krw")
 * reservationId   – 해당 금액이 대응되는 예약 ID
 */
@Data
@NoArgsConstructor
public class CreatePaymentIntentRequest {
    private int amount;
    private String currency;
    private int reservationId;
}