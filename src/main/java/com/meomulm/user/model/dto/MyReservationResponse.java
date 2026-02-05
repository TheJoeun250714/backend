package com.meomulm.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * vw_mypage_reservation 뷰 테이블로 받아오는 예약내역 응답 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyReservationResponse {
    // 숙소 ID
    private int accommodationId;
    // 숙소명
    private String accommodationName;

    // 객실 ID
    private int productId;
    // 객실명
    private String productName;
    // 객실 체크인 시간
    private String productCheckInTime;
    // 객실 체크아웃 시간
    private String productCheckOutTime;

    // 예약 ID
    private int reservationId;
    // 유저 ID
    private int userId;
    // 유저 체크인 날짜
    private String checkInDate;
    // 유저 체크아웃 날짜
    private String checkOutDate;
    // 예약 상태
    private String status;
}
