package com.meomulm.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    // 예약 아이디
    private int reservationId;
    // 유저 아이디
    private int userId;
    // 객실 아이디
    private int productId;
    // 예약자명
    private String bookerName;
    // 예약자 이메일
    private String bookerEmail;
    // 예약자 전화번호
    private String bookerPhone;
    // 체크인 날짜
    private Date checkInDate;
    // 체크아웃 날짜
    private Date checkOutDate;
    // 예약 인원 수
    private int guestCount;
    // 예약 상태
    private String status;
    // 총 가격
    private int totalPrice;
    // 생성일자
    private String createdAt;
    // 변경일자
    private String updatedAt;
}