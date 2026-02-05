package com.meomulm.product.payment.model.mapper;


import com.meomulm.product.payment.model.dto.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    // 결제  추가
    void insertPayment(Payment payment);

    // 결제 삭제
    void deletePayment(int reservationId);
}