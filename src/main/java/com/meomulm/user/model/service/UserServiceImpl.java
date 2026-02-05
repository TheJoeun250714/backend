package com.meomulm.user.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.common.util.FileUploadService;
import com.meomulm.common.util.ValidateUtil;
import com.meomulm.reservation.model.dto.Reservation;
import com.meomulm.user.model.dto.MyReservationResponse;
import com.meomulm.user.model.dto.User;
import com.meomulm.user.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileUploadService fileUploadService;
    private final ValidateUtil validateUtil;



    // ==========================================
    //                  My Page
    // ==========================================

    /**
     * 회원정보 조회
     * @param userId 유저 ID
     * @return 유저 객체
     */
    @Override
    public User getUserInfoById(int userId) {
        log.info("💡 회원정보 조회 시작. userId: {}", userId);
        User user = userMapper.selectUserInfoById(userId);

        if (user == null) {
            log.warn("⚠️ 조회 결과 - 사용자 없음. userId: {}", userId);
            throw new BadRequestException("사용자를 찾을 수 없습니다.");
        }

        log.info("✅ 회원정보 조회 성공. userId: {}", user.getUserId());
        return user;
    }

    /**
     * 회원정보 수정
     * @param user 유저 객체
     * @param currentUserId 현재 로그인한 유저 ID
     */
    @Transactional
    @Override
    public void putUserInfo(User user, int currentUserId) {
        // 정규식 검증 (이름, 전화번호)
        validateUtil.validateName(user.getUserName());
        validateUtil.validatePhone(user.getUserPhone());

        log.info("💡 회원정보 수정 시작. userId: {}", currentUserId);
        userMapper.updateUserInfo(user.getUserName(), user.getUserPhone(), currentUserId);

        log.info("✅ 회원정보 수정 완료. userId: {}", currentUserId);
    }

    /**
     * 회원 예약 내역 조회
     * @param userId 유저 ID
     * @return 예약 DTO 리스트
     */
    @Override
    public List<MyReservationResponse> getUserReservationById(int userId) {
        log.info("💡 예약내역 조회 시작. userId: {}", userId);
        List<MyReservationResponse> reservations = userMapper.selectUserReservationById(userId);

        if (reservations == null) {
            log.warn("⚠️ 조회 결과 - 예약내역 없음. userId: {}", userId);
            throw new BadRequestException("예약이 존재하지 않습니다.");
        }

        log.info("✅ 예약내역 조회 성공. userId: {}", userId);
        return reservations;
    }

    /**
     * 프로필 사진 수정
     * @param userProfileImage 사용자 프로필 이미지 경로
     * @param userId 유저 ID
     */
    @Transactional
    @Override
    public void updateProfileImage(String userProfileImage, int userId) {
        log.info("💡 프로필 사진 수정 시작. userId: {}", userId);
        if (userProfileImage == null || userProfileImage.isEmpty()) {
            log.warn("⚠️ 프로필 이미지가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("프로필 사진이 존재하지 않습니다.");
        }

        // MultipartFile -> String
        // String saveImagePath = fileUploadService.uploadProfileImage(userProfileImage);

        log.info("💡 프로필 사진 수정 시작. userId: {}", userId);
        userMapper.updateProfileImage(userProfileImage, userId);

        log.info("✅ 프로필 사진 수정 성공. userId: {}, userProfileImage: {}", userId, userProfileImage);
    }

    /**
     * 현재 비밀번호 확인
     * @param userId 유저 ID
     * @param inputPassword 현재 비밀번호
     */
    @Override
    public void getCurrentPassword(int userId, String inputPassword) {
        log.info("💡 현재 비밀번호 확인 시작. userId: {}", userId);
        if (inputPassword == null || inputPassword.isEmpty()) {
            log.warn("⚠️ 비밀번호가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("입력한 비밀번호가 존재하지 않습니다.");
        }

        String currentPassword = userMapper.selectCurrentPassword(userId);

        if (!bCryptPasswordEncoder.matches(inputPassword, currentPassword)) {
            log.warn("⚠️ 비밀번호 불일치. inputPassword: {}, currentPassword: {}", inputPassword, currentPassword);
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        log.info("✅ 현재 비밀번호 조회 성공. userId: {}", userId);
    }

    /**
     * 비밀번호 수정
     * @param userId 유저 ID
     * @param newPassword 새 비밀번호
     */
    @Transactional
    @Override
    public void putMyPagePassword(int userId, String newPassword) {
        log.info("💡 비밀번호 수정 시작. userId: {}", userId);
        if (newPassword == null || newPassword.isEmpty()) {
            log.warn("⚠️ 새 비밀번호가 존재하지 않음. userId: {}", userId);
            throw new NotFoundException("입력한 새 비밀번호가 존재하지 않습니다.");
        }

        // 정규식 검증 (새 비밀번호)
        validateUtil.validatePassword(newPassword);

        userMapper.updateMyPagePassword(userId, bCryptPasswordEncoder.encode(newPassword));
        log.info("✅ 비밀번호 수정 성공. userId: {}", userId);
    }

    /**
     * 회원정보 삭제
     * @param userId 유저 ID
     */
    @Transactional
    @Override
    public void deleteUser(int userId) {
        log.info("💡 회원정보 삭제 시작. userId: {}", userId);
        userMapper.deleteUser(userId);
        log.info("✅ 회원정보 삭제 성공. userId: {}", userId);
    }

    // ==========================================
    //               Signup / Login
    // ==========================================

    /**
     * 회원가입
     * @param user 유저 객체
     */
    @Transactional
    @Override
    public void signup(User user) {
        User existingEmail = userMapper.selectUserByUserEmail(user.getUserEmail());

        if (existingEmail != null) {
            log.warn("❌ 이미 존재하는 이메일 : {}", existingEmail);
            throw new NotFoundException("이미 존재하는 이메일입니다.");
        }

        User existingPhone = userMapper.selectUserByUserPhone(user.getUserPhone());
        if (existingPhone != null) {
            log.warn("❌ 이미 존재하는 전화번호 : {}", existingPhone);
            throw new NotFoundException("이미 존재하는 전화번호입니다.");
        }

        // 정규식 검증
        validateUtil.validateEmail(user.getUserEmail());
        validateUtil.validatePassword(user.getUserPassword());
        validateUtil.validateName(user.getUserName());
        validateUtil.validatePhone(user.getUserPhone());
        validateUtil.validateBirth(user.getUserBirth());


        String encodePw = bCryptPasswordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodePw);
        userMapper.insertUser(user);
        log.info("✅ 회원가입 완료 - 이메일 {}, 사용자명 : {}", user.getUserEmail(), user.getUserName());
    }

    /**
     * 로그인
     * @param userEmail     로그인 할 유저 이메일
     * @param userPassword  로그인 할 유저 비밀번호
     * @return 유저 객체
     */
    @Override
    public User login(String userEmail, String userPassword) {
        User user = userMapper.selectUserLogin(userEmail);

        if (bCryptPasswordEncoder.matches(userPassword, user.getUserPassword())) {
            log.info("✅ 로그인 성공 - 이메일 : {}", userEmail);
            return user;
        }

        throw new NotFoundException("로그인 정보 없음");
    }

    /**
     * 아이디 찾기
     * @param userName  유저 이름
     * @param userPhone 유저 전화번호
     * @return 유저 이메일
     */
    @Override
    public String getUserFindId(String userName, String userPhone) {
        User user = userMapper.selectUserFindId(userName, userPhone);

        if (user != null) {
            log.info("✅ 아이디 찾기 성공 : {}", user.getUserEmail());
            return user.getUserEmail();
        }
        throw new NotFoundException("이메일 정보 없음");
    }

    /**
     * 비밀번호 찾기
     * @param userEmail 유저 이메일
     * @param userBirth 유저 생년
     * @return 유저 ID
     */
    @Override
    public int getUserFindPassword(String userEmail, String userBirth) {
        User user = userMapper.selectUserFindPassword(userEmail, userBirth);


        if (user == null) {
            throw new NotFoundException("유저 정보 없음");
        }

        log.info("userId : {}", user.getUserId());

        log.info("✅ 유저 정보 확인 성공 이메일 : {}, 생년: {}", userEmail, userBirth);
        return user.getUserId();
    }

    /**
     * 비밀번호 변경 (로그인 페이지)
     * @param userId        유저 ID
     * @param newPassword   새 비밀번호
     */
    @Transactional
    @Override
    public void patchUserPassword(int userId, String newPassword) {
        log.info("💡 비밀번호 수정 시작 userId: {}", userId);

        if (newPassword == null || newPassword.isEmpty()) {
            log.warn("⚠️ 새 비밀번호가 존재하지 않음 userId: {}", userId);
            throw new BadRequestException("새 비밀번호가 존재하지 않습니다.");
        }

        validateUtil.validatePassword(newPassword);

        int result = userMapper.updateUserPassword(userId, bCryptPasswordEncoder.encode(newPassword));

        if(result == 0) {
            throw new BadRequestException("비밀번호 변경 실패");
        }
        log.info("✅ 비밀번호 수정 성공 userId: {}", userId);
    }

    /**
     * 이메일 조회
     * @param userEmail 유저 이메일
     * @return 유저 객체
     */
    @Override
    public User getUserByUserEmail(String userEmail) {
        return userMapper.selectUserByUserEmail(userEmail);
    }

    /**
     * 이메일 중복 확인
     * @param userEmail 유저 이메일
     * @return
     */
    @Override
    public boolean existsByUserEmail(String userEmail) {
        return userMapper.existsByUserEmail(userEmail) > 0;
    }

    /**
     * 전화번호 중복 확인
     * @param userPhone 유저 전화번호
     * @return
     */
    @Override
    public boolean existsByUserPhone(String userPhone) {
        return userMapper.existsByUserPhone(userPhone) > 0;
    }
}