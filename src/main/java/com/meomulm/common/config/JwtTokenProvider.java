package com.meomulm.common.config;

import com.meomulm.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtUtil jwtUtil;

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public Authentication getAuthentication(String token) {
        int userId = jwtUtil.getUserIdFromToken(token);

        return new UsernamePasswordAuthenticationToken(
                String.valueOf(userId),
                null,
                Collections.emptyList()
        );
    }
}