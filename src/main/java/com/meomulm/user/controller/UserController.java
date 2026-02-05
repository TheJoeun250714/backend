package com.meomulm.user.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.user.model.dto.CurrentPassword;
import com.meomulm.user.model.dto.MyReservationResponse;
import com.meomulm.user.model.dto.NewPassword;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    // ==========================================
    //                  My Page
    // ==========================================
    private final AuthUtil authUtil;
    private final UserService userService;

    /**
     * 회원정보 조회
     * @param authHeader JWT 토큰 헤더
     * @return User 객체 + 상태코드 200
     */
    @GetMapping
    public ResponseEntity<User> getUserInfoById(
            @RequestHeader("Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        User user = userService.getUserInfoById(currentUserId);

        return ResponseEntity.ok(user);
    }

    /**
     * 회원정보 수정
     * @param authHeader JWT 토큰 헤더
     * @param user 유저 객체
     * @return 상태코드 200
     */
    @PutMapping("/userInfo")
    public ResponseEntity<Void> putUserInfo(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody User user){
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.putUserInfo(user, currentUserId);

        return ResponseEntity.ok().build();
    }

    /**
     * 회원 예약 내역 조회
     * @param authHeader JWT 토큰 헤더
     * @return 예약내역 DTO 리스트 + 상태코드 200
     */
    @GetMapping("/reservation")
    public ResponseEntity<List<MyReservationResponse>> getUserReservationById(@RequestHeader("Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        List<MyReservationResponse> reservations = userService.getUserReservationById(currentUserId);

        return ResponseEntity.ok(reservations);
    }

    /**
     * 프로필 사진 수정
     * @param authHeader JWT 토큰 헤더
     * @param userProfileImage 새로 저장할 프로필 이미지
     * @return 상태코드 200
     */
    @PatchMapping("/profileImage")
    public ResponseEntity<Void> updateProfileImage(@RequestHeader("Authorization") String authHeader,
                                                   @RequestBody String userProfileImage) {
                                                   // @RequestPart MultipartFile userProfileImage) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.updateProfileImage(userProfileImage, currentUserId);

        return ResponseEntity.ok().build();
    }

    /**
     * 현재 비밀번호 확인
     * @param authHeader JWT 토큰 헤더
     * @param currentPassword 입력된 현재 비밀번호
     * @return 상태코드 200
     */
    @PostMapping("/currentPassword")
    public ResponseEntity<Void> getCurrentPassword(@RequestHeader("Authorization") String authHeader,
                                                   @RequestBody CurrentPassword currentPassword) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.getCurrentPassword(currentUserId, currentPassword.getCurrentPassword());

        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 수정
     * @param authHeader JWT 토큰 헤더
     * @param newPassword 입력된 새 비밀번호
     * @return 상태코드 200
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> putMyPagePassword(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody NewPassword newPassword) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.putMyPagePassword(currentUserId, newPassword.getNewPassword());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        userService.deleteUser(currentUserId);

        return ResponseEntity.ok().build();
    }
}