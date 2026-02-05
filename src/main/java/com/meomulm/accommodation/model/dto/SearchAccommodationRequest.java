package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAccommodationRequest {
//    // 숙소 검색 키워드
//    private String keyword;
//    // 숙소 주소
//    private String accommodationAddress;
//    // 경도
//    private double longitude;
//    // 위도
//    private double latitude;


    // --- 기본 검색 파라미터 ---
    private String keyword;               // 숙소명 또는 지역명 키워드
    private String accommodationAddress;  // 인기 숙소 조회용 주소
    private Double longitude;             // 현위치 경도
    private Double latitude;              // 현위치 위도

    /*
    위도(latitude)와 경도(longitude)를 double에서 Double (Wrapper 클래스)로 변경하는 것이 안전.
    기본형 double은 값이 없으면 0.0이 들어가기 때문에 좌표가 실제로 0.0인 것인지 값이 없는 것인지
    구분하기 위해 null 처리가 가능한 객체 타입을 사용하는 것이 안전하다.
    -> 조회 방식(키워드/현위치)에 따라 null 허용해야 함.
     */

    // --- 필터링 파라미터 (추가됨) ---
    private List<String> facilities;      // 편의시설 리스트 (has_parking, has_wifi 등)
    private List<String> types;           // 숙소 종류 리스트 (HOTEL, MOTEL 등)
    private Integer minPrice;             // 최소 가격
    private Integer maxPrice;             // 최대 가격

}