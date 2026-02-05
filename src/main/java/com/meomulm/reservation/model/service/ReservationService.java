package com.meomulm.reservation.model.service;

import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.dto.ReservationDeleteRequest;
import com.meomulm.reservation.model.dto.ReservationUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public interface ReservationService {

    /**
     * 예약 추가
     * @param reservation 예약 DTO
     */
    void postReservation(Reservation reservation);

    /**
     * 예약 수정
     * @param reservation 예약 DTO
     * @param loginUserId 로그인한 유저 ID
     */
    void patchReservation(ReservationUpdateRequest reservation, int loginUserId);

    /**
     * 예약 취소 (상태만 변경)
     * @param reservation 예약 DTO
     * @param loginUserId 로그인한 유저 ID
     */
    void putReservation(ReservationDeleteRequest reservation, int loginUserId);

    /**
     * 예약 삭제 (미결제 종료)
     * @param reservation 예약 DTO
     * @param loginUserId 로그인한 유저 ID
     */
    void deleteReservation(ReservationDeleteRequest reservation, int loginUserId);
}