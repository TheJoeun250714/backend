package com.meomulm.reservation.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.dto.ReservationDeleteRequest;
import com.meomulm.reservation.model.dto.ReservationUpdateRequest;
import com.meomulm.reservation.model.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final AuthUtil authUtil;

    /**
     * 예약 추가
     *
     * 기존과의 차이:
     *   - postReservation() 뒤에 reservation.getReservationId() 를 body로 반환
     *   - Flutter 앱에서 이 ID 를 받아 결제 화면에 전달
     *
     * 참고: MyBatis insertReservation 에 useGeneratedKeys="true" 설정이
     *       있어야 reservation.getReservationId() 가 자동 채워진다.
     *       reservationMapper.xml 의 <insert> 태그에
     *         useGeneratedKeys="true" keyProperty="reservationId"
     *       를 추가한다.
     */
    @PostMapping
    public ResponseEntity<Map<String, Integer>> postReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Reservation reservation) {

        int loginUserId = authUtil.getCurrentUserId(authHeader);
        reservation.setUserId(loginUserId);
        reservationService.postReservation(reservation);

        // 생성된 reservationId를 응답 body로 반환
        return ResponseEntity.ok(Map.of("reservationId", reservation.getReservationId()));
    }

    /**
     * 예약 수정 (기존 유지)
     */
    @PatchMapping
    public ResponseEntity<Void> patchReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReservationUpdateRequest reservation) {

        int loginUserId = authUtil.getCurrentUserId(authHeader);
        reservationService.patchReservation(reservation, loginUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * 예약 취소 (기존 유지)
     */
    @PutMapping
    public ResponseEntity<Void> putReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReservationDeleteRequest reservation) {

        int loginUserId = authUtil.getCurrentUserId(authHeader);
        reservationService.putReservation(reservation, loginUserId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteReservation(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReservationDeleteRequest reservation) {

        int loginUserId = authUtil.getCurrentUserId(authHeader);
        reservationService.deleteReservation(reservation, loginUserId);
        return ResponseEntity.ok().build();
    }
}