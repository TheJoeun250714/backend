package com.meomulm.user.model.service;

import com.meomulm.user.model.dto.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NaverServiceImpl {

    private final RestTemplate restTemplate = new RestTemplate();

    public User getNaverUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> res = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map body = res.getBody();
        if (body == null) return null;

        if (!"00".equals(body.get("resultcode"))) return null;

        Map response = (Map) body.get("response");
        if (response == null) return null;

        User user = new User();
        user.setUserEmail((String) response.get("email"));
        user.setUserName((String) response.get("name"));
        return user;
    }
}

