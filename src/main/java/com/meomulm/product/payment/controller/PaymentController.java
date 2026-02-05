package com.meomulm.product.payment.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.common.util.JwtUtil;
import com.meomulm.product.payment.model.dto.ConfirmPaymentRequest;
import com.meomulm.product.payment.model.dto.CreatePaymentIntentRequest;
import com.meomulm.product.payment.model.dto.CreatePaymentIntentResponse;
import com.meomulm.product.payment.model.dto.Payment;
import com.meomulm.product.payment.model.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final JwtUtil jwtUtil;
    private final PaymentService paymentService;
    private final AuthUtil authUtil;

    /**
     * 결제정보 추가
     */
    @PostMapping("/{reservationId}")
    public ResponseEntity<Void> postPayment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Payment payment,
            @PathVariable int reservationId) {

        authUtil.getCurrentUserId(authHeader);
        int loginUserId = jwtUtil.getUserIdFromToken(authHeader.substring(7));;
        paymentService.postPayment(payment, reservationId, loginUserId);
        return ResponseEntity.ok().build();
    }

    // ============================================================
    // Stripe 테스트 API 엔드포인트
    // ============================================================

    /**
     * ① PaymentIntent 생성
     *
     * Flutter 앱 → POST /api/payment/stripe/create-payment-intent
     *   body : { "amount": 70000, "currency": "krw", "reservationId": 5 }
     *
     * 서버는 Stripe SDK 로 PaymentIntent 를 생성하고
     * client_secret 만 앱에 돌려준다.
     *
     * @param authHeader     JWT 토큰
     * @param request        금액 / 통폐화 / 예약ID
     * @return               { "clientSecret": "pi_xxxxx_secret_xxxxx" }
     */
    @PostMapping("/stripe/create-payment-intent")
    public ResponseEntity<CreatePaymentIntentResponse> createPaymentIntent(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreatePaymentIntentRequest request) {

        authUtil.getCurrentUserId(authHeader);
        CreatePaymentIntentResponse response = paymentService.createPaymentIntent(request);
        return ResponseEntity.ok(response);
    }

    /**
     * ② 결제 확인 (Flutter SDK 로 confirmPayment 완료 후 호출)
     *
     * Flutter 앱 → POST /api/payment/stripe/confirm
     *   body : { "paymentIntentId": "pi_xxxxx", "reservationId": 5 }
     *
     * 서버는 Stripe SDK 로 PaymentIntent 상태를 다시 조회하여
     * 실제로 "succeeded" 인지 확인한 후 DB 에 결제 정보를 저장한다.
     *
     * @param authHeader     JWT 토큰
     * @param request        paymentIntentId + reservationId
     * @return               200 OK (성공) / 400~500 (실패)
     */
    @PostMapping("/stripe/confirm")
    public ResponseEntity<Void> confirmPayment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ConfirmPaymentRequest request) {

        authUtil.getCurrentUserId(authHeader);
        paymentService.confirmPayment(request);
        return ResponseEntity.ok().build();
    }
}
