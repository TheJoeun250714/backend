package com.meomulm.product.payment.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    // 결제  아이디
    private int paymentId;
    // 예약 아이디
    private int reservationId;
    // 결제 수단
    private String paymentMethod;
    // 결제 금액
    private int paidAmount;
    // 결제 상태
    private String status;
    // 결제 일자
    private String paidAt;
}