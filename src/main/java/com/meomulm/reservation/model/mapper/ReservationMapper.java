package com.meomulm.reservation.model.mapper;


import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.reservation.model.dto.ReservationDTO;
import com.meomulm.reservation.model.dto.ReservationUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {

//    // 내일 체크인 예정인 예약 리스트 조회
//    List<Reservation> selectTomorrowCheckInList();

//    // 오늘 체크아웃 예정인 예약 리스트 조회
//    List<Reservation> selectTodayCheckOutList();

    List<ReservationDTO> selectReservationWithNames(@Param("type") String type);


    // 예약 아이디를 기반으로 예약 조회
    Reservation selectReservationById(int reservationId);

    // 예약 추가
    void insertReservation(Reservation reservation);

    // 예약 수정
    void updateReservation(ReservationUpdateRequest reservation);
    
    // 예약 상태 변경 (결제 후)
    void updateStatusToPaid(int reservationId);

    // 예약 상태 변경 (이용 후)
    void updateStatusToUsed(int reservationId);

    // 예약 취소 (상태 변경)
    void putReservation(int reservationId);

    // 예약 삭제(미결제 종료)
    void deleteReservation(int reservationId);
}