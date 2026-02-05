package com.meomulm.accommodation.model.mapper;


import com.meomulm.accommodation.model.dto.AccommodationDetail;
import com.meomulm.accommodation.model.dto.AccommodationImage;
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccommodationMapper {
    // 숙소 아이디로 숙소 이미지 1개 반환
    AccommodationImage selectAccommodationImageById(int accommodationId);

    // 숙소 아이디를 기반으로 이미지 리스트 반환
    List<AccommodationImage> selectAccommodationImagesById(int accommodationId);

    // 숙소명, 지역명으로 숙소 검색
    List<SearchAccommodationResponse> selectAccommodationByKeyword(String keyword);



    // 키워드 / 현위치 / 필터링 통합 조회
    List<SearchAccommodationResponse> selectAccommodations(SearchAccommodationRequest request);

    // 최근 본 숙소 조회
    List<SearchAccommodationResponse> selectRecentAccommodations(List<Integer> ids);

    // 지역별 가격 낮은 순 숙소 12개 조회
    List<SearchAccommodationResponse> selectAccommodationPopularByAddress(String accommodationAddress);

    // 현재 위치를 기반으로 반경 5KM 숙소 조회
    List<SearchAccommodationResponse> selectAccommodationByLocation(double accommodationLatitude, double accommodationLongitude);

    // 숙소 상세 검색
    AccommodationDetail selectAccommodationDetailById(int accommodationId);
}