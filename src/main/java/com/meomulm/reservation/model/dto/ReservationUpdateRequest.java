package com.meomulm.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationUpdateRequest {
    private int reservationId;
    private String bookerName;
    private String bookerEmail;
    private String bookerPhone;
}
