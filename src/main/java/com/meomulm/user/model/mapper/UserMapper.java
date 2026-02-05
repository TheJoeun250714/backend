package com.meomulm.user.model.mapper;


import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.user.model.dto.MyReservationResponse;
import com.meomulm.user.model.dto.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {


    // 오늘이 생일인 회원 리스트 조회
    List<User> selectTodayBirthdayList();

    // 회원정보 조회
    User selectUserInfoById(int userId);

    // 회원정보 수정
    void updateUserInfo(String userName, String userPhone, int currentUserId);

    // 회원 예약 내역 조회
    List<MyReservationResponse> selectUserReservationById(int userId);

    //프로필 사진 수정
    void updateProfileImage(String userProfileImage, int userId);

    // 현재 비밀번호 확인
    String selectCurrentPassword(int userId);

    // 비밀번호 수정 (마이페이지)
    void updateMyPagePassword(int userId, String userPassword);

    // 회원정보 삭제
    void deleteUser(int userId);

    // Signup / Login

    // 회원가입
    void insertUser(User user);

    // 로그인
    User selectUserLogin(String userEmail);

    // 아이디 찾기
    User selectUserFindId(String userName, String userPhone);

    // 비밀번호 찾기
    User selectUserFindPassword(String userEmail, String userBirth);

    // 비밀번호 수정 (로그인페이지)
    int updateUserPassword(int userId, String userPassword);

    // 이메일 조회
    User selectUserByUserEmail(String userEmail);

    // 비밀번호 조회
    User selectUserByUserPhone(String userPhone);

    // 이메일 중복 체크
    int existsByUserEmail(String userEmail);

    // 전화번호 중복 체크
    int existsByUserPhone(String userPhone);

}