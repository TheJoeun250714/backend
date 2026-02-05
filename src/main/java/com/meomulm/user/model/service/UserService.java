package com.meomulm.user.model.service;

import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.user.model.dto.MyReservationResponse;
import com.meomulm.user.model.dto.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    // ==========================================
    //                  My Page
    // ==========================================
    /**
     * 회원정보 조회
     * @param userId 유저 ID
     * @return 유저 객체
     */
    User getUserInfoById(int userId);

    /**
     * 회원정보 수정
     * @param user 유저 객체
     * @param currentUserId 현재 로그인한 유저 ID
     */
    void putUserInfo(User user, int currentUserId);

    /**
     * 회원 예약 내역 조회
     * @param userId 유저 ID
     * @return 예약 DTO 리스트
     */
    List<MyReservationResponse> getUserReservationById(int userId);

    /**
     * 프로필 사진 수정
     * @param userProfileImage 사용자 프로필 이미지 경로
     */
    void updateProfileImage(String userProfileImage, int userId);

    /**
     * 현재 비밀번호 확인
     * @param userId 유저 ID
     * @param inputPassword 현재 비밀번호
     */
    void getCurrentPassword(int userId, String inputPassword);

    /**
     * 비밀번호 수정 (마이페이지)
     * @param userId 유저 ID
     * @param newPassword 새 비밀번호
     */
    void putMyPagePassword(int userId, String newPassword);

    /**
     * 회원정보 삭제
     * @param userId 유저 ID
     */
    void deleteUser(int userId);

    // ==========================================
    //               Signup / Login
    // ==========================================

    /**
     * 회원가입
     * @param user 유저 객체
     */
    void signup(User user);

    /**
     * 로그인
     * @param userEmail     로그인 할 유저 이메일
     * @param userPassword  로그인 할 유저 비밀번호
     * @return  유저 객체
     */
    User login(String userEmail, String userPassword);

    /**
     * 아이디 찾기
     * @param userName  유저 이름
     * @param userPhone 유저 전화번호
     * @return  유저 이메일
     */
    String getUserFindId(String userName, String userPhone);

    /**
     * 비밀번호 찾기
     * @param userEmail 유저 이메일
     * @param userBirth 유저 생년
     * @return 유저 ID
     */
    int getUserFindPassword(String userEmail, String userBirth);

    /**
     * 비밀번호 변경 (로그인 페이지)
     * @param userId        유저 ID
     * @param newPassword   새 비밀번호
     */
    void patchUserPassword(int userId, String newPassword);

    /**
     * 이메일 조회
     * @param userEmail 유저 이메일
     * @return 유저 객체
     */
    User getUserByUserEmail(String userEmail);

    /**
     * 이메일 중복 체크
     * @param userEmail 유저 이메일
     * @return
     */
    boolean existsByUserEmail(String userEmail);

    /**
     * 전화번호 중복 체크
     * @param userPhone 유저 전화번호
     * @return
     */
    boolean existsByUserPhone(String userPhone);

}