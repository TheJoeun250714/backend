package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    // 숙소 아이디
    private int accommodationId;
    // 숙소명
    private String accommodationName;
    // 숙소 주소
    private String accommodationAddress;
    // 숙소 연락처
    private String accommodationPhone;
    // 경도
    private double accommodationLongitude;
    // 위도
    private double accommodationLatitude;
    // 지역코드
    private String accommodationRegionCode;
    // 공공데이터 API 원본 고유 ID (중복 방지 및 식별용)
    private String contentId;
    // 관광 타입 ID
    private String contentTypeId;
    // 카테고리 테이블 참조 ID
    private int categoryId;
}
