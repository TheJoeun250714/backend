package com.meomulm.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 유저 ID
    private int userId;
    // 유저 이메일
    private String userEmail;
    // 유저 비밀번호
    private String userPassword;
    // 유저 이름
    private String userName;
    // 유저 연락처
    private String userPhone;
    // 유저 생년월일
    private String userBirth;
    // 유저 프로필 이미지
    private String userProfileImage;
    // 생성일자
    private String createdAt;
}
