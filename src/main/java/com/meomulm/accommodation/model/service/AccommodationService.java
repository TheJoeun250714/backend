package com.meomulm.accommodation.model.service;

import com.meomulm.accommodation.model.dto.AccommodationDetail;
import com.meomulm.accommodation.model.dto.AccommodationImage;
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;

import java.util.List;

public interface AccommodationService {
    AccommodationImage getAccommodationImageById(int accommodationId);

    /**
     * 숙소 ID로 숙소 이미지 리스트 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 이미지 DTO 리스트
     */
    List<AccommodationImage> getAccommodationImagesById(int accommodationId);

    /**
     * 키워드로 숙소 검색 : 돋보기 검색
     * @param keyword 숙소명 또는 지역명
     * @return 숙소검색 응답 DTO 리스트
     */
    List<SearchAccommodationResponse> getAccommodationByKeyword(String keyword);


    /**
     * 키워드 / 현위치 / 필터링 통합 조회
     * @param request
     * @return
     */
    List<SearchAccommodationResponse> searchAccommodations(SearchAccommodationRequest request);

    /**
     * 최근 본 숙소
     * @param ids 최근 본 숙소 아이디 리스트
     * @return 숙소검색 DTO 리스트
     */
    List<SearchAccommodationResponse> getRecentAccommodations(List<Integer> ids);

    /**
     * 지역별 가격 낮은 숙소 12개 조회
     * @param accommodationAddress 숙소 주소
     * @return 숙소검색 응답 DTO 리스트
     */
    List<SearchAccommodationResponse> getAccommodationPopularByAddress(String accommodationAddress);

    /**
     * 현재위치 기반 반경 5km 내 숙소 조회 : 지도 검색
     * @param accommodationLatitude 숙소 경도
     * @param accommodationLongitude 숙소 위도
     * @return 숙소검색 응답 DTO 리스트
     */
    List<SearchAccommodationResponse> getAccommodationByLocation(double accommodationLatitude, double accommodationLongitude);

    /**
     * 숙소 ID로 숙소 상세정보 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 상세정보 DTO
     */
    AccommodationDetail getAccommodationDetailById(int accommodationId);
}