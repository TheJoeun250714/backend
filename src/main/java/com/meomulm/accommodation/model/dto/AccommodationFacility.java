package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationFacility {
    // 숙소 편의시설 아이디
    private int accommodationFacilityId;
    // 숙소 아이디
    private int accommodationId;
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
}
