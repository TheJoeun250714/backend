package com.meomulm.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    // 로그인에 필요한 회원 이메일
    private String userEmail;

    // 로그인에 필요한 회원 비밀번호
    private String userPassword;
}