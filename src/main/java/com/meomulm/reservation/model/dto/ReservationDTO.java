package com.meomulm.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

    // reservations 테이블 관련
    private int reservationId;
    private int userId;

    // product 테이블 관련
    private int productId;

    // accommodation 테이블 관련 (JOIN 결과)
    private String accommodationName;

}
