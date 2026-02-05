package com.meomulm.product.payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * POST /api/payment/stripe/create-payment-intent 응답 본문
 *
 * clientSecret – Stripe PaymentIntent의 client_secret
 *                Flutter SDK 에서 이 값으로 confirmPayment() 호출
 */
@Data
@AllArgsConstructor
public class CreatePaymentIntentResponse {
    private String clientSecret;
}