package com.meomulm.favorite.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {
    // 찜 아이디
    private int favoriteId;
    // 유저 아이디
    private int userId;
    // 숙소 아이디
    private int accommodationId;
    // 찜 생성일자
    private String createdAt;
}

