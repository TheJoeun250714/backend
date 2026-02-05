package com.meomulm.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDeleteRequest {
    // 예약 아이디
    private int reservationId;
}
