package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAccommodationResponse {

    // 숙소 아이디
    private int accommodationId;
    // 숙소명
    private String accommodationName;
    // 숙소 주소
    private String accommodationAddress;
    // 위도
    private double accommodationLatitude;
    // 경도
    private double accommodationLongitude;
    // 객실 최소 가격
    private int minPrice;

    // 카테고리 코드
    private String categoryCode;


    // 숙소 이미지 리스트
    private List<AccommodationImage> accommodationImages;
}
