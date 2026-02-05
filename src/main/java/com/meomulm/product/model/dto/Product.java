package com.meomulm.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    // 객실 아이디
    private int productId;
    // 숙소 아이디
    private int accommodationId;
    // 객실명
    private String productName;
    // 객실 체크인 시간
    private String productCheckInTime;
    // 객실 체크아웃 시간
    private String productCheckOutTime;
    // 객실 가격
    private int productPrice;
    // 객실 기존 수용 인원
    private int productStandardNumber;
    // 객실 최대 수용 인원
    private int productMaximumNumber;
    // 객실 수
    private int productCount;

    // 객실 편의시설 객체
    private ProductFacility facility;
    // 객실 이미지 리스트
    private List<ProductImage> images;
}