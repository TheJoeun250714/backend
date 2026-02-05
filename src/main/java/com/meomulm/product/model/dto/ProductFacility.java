package com.meomulm.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFacility {
    // 객실 편의시설 아이디
    private int productFacilityId;
    // 객실 아이디
    private int productId;
    // 욕조 보유 여부
    private boolean hasBath;
    // 에어컨 보유 여부
    private boolean hasAirCondition;
    // 냉장고 보유 여부
    private boolean hasRefrigerator;
    // 비데 보유 여부
    private boolean hasBidet;
    // TV 보유 여부
    private boolean hasTv;
    // PC 보유 여부
    private boolean hasPc;
    // 인터넷 / 네트워크 여부
    private boolean hasInternet;
    // 세면도구 여부
    private boolean hasToiletries;

}