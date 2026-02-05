package com.meomulm.user.model.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meomulm.user.model.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoServiceImpl {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 카카오 사용자 정보 조회
     * @param accessToken 카카오 액세스 토큰
     * @return User 객체
     */
    public User getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            log.info("💡 카카오 사용자 정보 요청 시작");
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.info("✅ 카카오 API 응답: {}", response.getBody());

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            User user = new User();

            // 카카오 계정 정보
            JsonNode kakaoAccount = jsonNode.get("kakao_account");
            if(kakaoAccount != null) {
                // 이메일
                if(kakaoAccount.has("email")) {
                    user.setUserEmail(kakaoAccount.get("email").asText());
                }

                // 이름 (프로필 닉네임 사용)
                if(kakaoAccount.has("profile")) {
                    JsonNode profile = kakaoAccount.get("profile");
                    if(profile.has("nickname")) {
                        user.setUserName(profile.get("nickname").asText());
                    }
                }

                // 전화번호 (선택사항)
                if(kakaoAccount.has("phone_number")) {
                    String phone = kakaoAccount.get("phone_number").asText();
                    user.setUserPhone(phone.replaceAll("[^0-9]", ""));
                }

                // 생년월일 (선택사항)
                if(kakaoAccount.has("birthyear") && kakaoAccount.has("birthday")) {
                    String birthyear = kakaoAccount.get("birthyear").asText();
                    String birthday = kakaoAccount.get("birthday").asText();
                    user.setUserBirth(birthyear + birthday);
                }

                // 프로필 이미지
                if(kakaoAccount.has("profile")) {
                    JsonNode profile = kakaoAccount.get("profile");
                    if(profile.has("profile_image_url")) {
                        user.setUserProfileImage(profile.get("profile_image_url").asText());
                    }
                }
            }

            log.info("✅ 카카오 사용자 정보 파싱 완료 - email: {}, name: {}", user.getUserEmail(), user.getUserName());
            return user;

        } catch (Exception e) {
            log.error("❌ 카카오 사용자 정보 조회 실패", e);
            return null;
        }
    }
}