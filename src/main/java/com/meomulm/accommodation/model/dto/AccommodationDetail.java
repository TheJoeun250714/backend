package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDetail {
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
    // 공공데이터 API 원본 고유 ID (중복 방지 및 식별용)
    private String contentId;

    // 주차 가능 여부
    private boolean hasParking;
    // 전기차 충전시설 보유 여부
    private boolean hasEvCharging;
    // 흡연구역 보유 여부
    private boolean hasSmokingArea;
    // 공용 와이파이 보유 여부
    private boolean hasPublicWifi;
    // 레저 시설 보유 여부
    private boolean hasLeisure;
    // 스포츠 시설 보유 여부
    private boolean hasSports;
    // 쇼핑 시설 보유 여부
    private boolean hasShopping;
    // 비즈니스 시설 보유 여부
    private boolean hasBusiness;
    // 식음료 시설 보유 여부
    private boolean hasFnb;

    // 숙소 이미지 리스트
    private List<AccommodationImage> accommodationImages;
}
