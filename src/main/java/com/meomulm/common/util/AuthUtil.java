package com.meomulm.common.util;

import com.meomulm.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final JwtUtil jwtUtil;

    public int getCurrentUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        try {
            return jwtUtil.getUserIdFromToken(authHeader.substring(7));
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }
}
