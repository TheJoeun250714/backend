package com.meomulm.accommodation.model.service;

import com.meomulm.accommodation.model.dto.AccommodationDetail;
import com.meomulm.accommodation.model.dto.AccommodationImage;
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;
import com.meomulm.accommodation.model.mapper.AccommodationMapper;
import com.meomulm.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationMapper accommodationMapper;


    /**
     * 숙소 ID로 숙소 대표이미지 1개 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 이미지 DTO
     */
    @Override
    public AccommodationImage getAccommodationImageById(int accommodationId) {
        AccommodationImage image = accommodationMapper.selectAccommodationImageById(accommodationId);
        return image;
    }

    /**
     * 숙소 ID로 숙소 이미지 리스트 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 이미지 DTO 리스트
     */
    @Override
    public List<AccommodationImage> getAccommodationImagesById(int accommodationId) {
        List<AccommodationImage> accommodationImages =
                accommodationMapper.selectAccommodationImagesById(accommodationId);
        return accommodationImages;
    }

    // 이미 존재하는 List 안의 객체들을 set 하는 역할

    /**
     * 각 숙소검색 응답 DTO의 이미지 변수(accommodationImages)에 숙소 이미지 리스트 저장
     * @param responses 숙소검색 응답 DTO
     */
    private void setAccommodationImages(List<SearchAccommodationResponse> responses) {
        for (SearchAccommodationResponse response : responses) {
            response.setAccommodationImages(
                    getAccommodationImagesById(response.getAccommodationId())
            );
        }
    }

    /**
     * 키워드로 숙소 검색 : 돋보기 검색
     * @param keyword 숙소명 또는 지역명
     * @return 숙소검색 응답 DTO 리스트
     */
    @Override
    public List<SearchAccommodationResponse> getAccommodationByKeyword(String keyword) {
        log.info("💡 숙소명, 지역명 숙소 검색 시작 - keyword={}", keyword);

        List<SearchAccommodationResponse> searchAccommodationResponse =
                accommodationMapper.selectAccommodationByKeyword(keyword);
        if (searchAccommodationResponse == null
                || searchAccommodationResponse.isEmpty()) {
            log.warn("❌ 숙소명, 지역명 숙소 검색 결과 없음 - keyword={}", keyword);
            throw new NotFoundException("해당 숙소가 존재하지 않습니다.");
        }
        setAccommodationImages(searchAccommodationResponse);
        log.info("✅ 숙소명, 지역명 숙소 검색 완료 - resultCount={}",
                searchAccommodationResponse.size());
        log.info("✅ 조회 결과 - {}", searchAccommodationResponse);
        return searchAccommodationResponse;
    }


    /**
     * 키워드 / 현위치 / 필터링 통합 조회
     * @param request 통합 dto
     * @return 숙소검색 응답 DTO 리스트
     */
    @Override
    public List<SearchAccommodationResponse> searchAccommodations(SearchAccommodationRequest request) {
        log.info("💡 숙소 검색 시작 - 조건: {}", request);

        // 1. 매퍼 호출 (동적 쿼리 수행)
        List<SearchAccommodationResponse> responses = accommodationMapper.selectAccommodations(request);

        if (responses == null || responses.isEmpty()) {
            log.warn("❌ 검색 결과 없음");
            throw new NotFoundException("조건에 맞는 숙소가 존재하지 않습니다.");
        }

        // 2. 각 숙소별 이미지 설정 (기존 메서드 재사용)
        setAccommodationImages(responses);

        log.info("✅ 검색 완료 - 결과 수: {}", responses.size());
        return responses;
    }


    /**
     * 최근 숙소 조회
     * @param ids 최근 본 숙소 아이디 리스트
     * @return 숙소검색 DTO 리스트
     */
    @Override
    public List<SearchAccommodationResponse> getRecentAccommodations(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        // 숙소 ID 목록 조회
        List<SearchAccommodationResponse> responses =
                accommodationMapper.selectRecentAccommodations(ids);

        // 이미지 세팅
        setAccommodationImages(responses);
        // log.info("✅ 최근 본 숙소 리스트 - responses={}", responses);

        return responses;
    }


    /**
     * 지역별 가격 낮은 숙소 12개 조회
     * @param accommodationAddress 숙소 주소
     * @return 숙소검색 응답 DTO 리스트
     */
    @Override
    public List<SearchAccommodationResponse> getAccommodationPopularByAddress(
            String accommodationAddress) {
        log.info("💡 지역 별 가격 낮은 순 숙소 12개 검색 시작 - accommodationAddress={}",
                accommodationAddress);

        List<SearchAccommodationResponse> searchAccommodationResponse =
                accommodationMapper.selectAccommodationPopularByAddress(
                        accommodationAddress);
        if (searchAccommodationResponse == null ||
                searchAccommodationResponse.isEmpty()) {
            log.warn("❌ 지역 별 가격 낮은 순 숙소 12개 검색 결과 없음 - accommodationAddress={}", accommodationAddress);
            throw new NotFoundException("해당 지역 숙소가 존재하지 않습니다.");
        }

        setAccommodationImages(searchAccommodationResponse);
        log.info("✅ 지역 별 가격 낮은 순 숙소 12개 숙소 검색 완료 - resultCount={}",
                searchAccommodationResponse.size());

        return searchAccommodationResponse;
    }

    /**
     * 현재위치 기반 반경 5km 내 숙소 조회 : 지도 검색
     * @param accommodationLatitude 숙소 경도
     * @param accommodationLongitude 숙소 위도
     * @return 숙소검색 응답 DTO 리스트
     */
    @Override
    public List<SearchAccommodationResponse> getAccommodationByLocation(
            double accommodationLatitude,
            double accommodationLongitude) {
        log.info("💡 지도 5km 반경 숙소 검색 시작 - latitude={}, longitude={}",
                accommodationLatitude, accommodationLongitude);


        List<SearchAccommodationResponse> searchAccommodationResponse =
                accommodationMapper.selectAccommodationByLocation(
                        accommodationLatitude,
                        accommodationLongitude);

        if (searchAccommodationResponse == null || searchAccommodationResponse.isEmpty()) {
            log.warn("❌ 지도 5km 반경 숙소 검색 결과 없음 - latitude={}, longitude={}", accommodationLatitude, accommodationLongitude);
            throw new NotFoundException("현재 위치 5km 내에 숙소가 존재하지 않습니다.");
        }

        setAccommodationImages(searchAccommodationResponse);
        log.info("✅ 지도 5km 반경 숙소 검색 완료 - resultCount={}", searchAccommodationResponse.size());

        return searchAccommodationResponse;
    }

    /**
     * 숙소 ID로 숙소 상세정보 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 상세정보 DTO
     */
    @Override
    public AccommodationDetail getAccommodationDetailById(int accommodationId) {
        log.info("💡 숙소 상세 검색 시작 - accommodationId={}", accommodationId);

        AccommodationDetail accommodationDetail = accommodationMapper.selectAccommodationDetailById(accommodationId);
        if (accommodationDetail == null) {
            log.warn("❌ 숙소 상세 검색 결과 없음 - accommodationId={}", accommodationId);
            throw new NotFoundException("숙소 상세 검색이 존재하지 않습니다.");
        }

        accommodationDetail.setAccommodationImages(getAccommodationImagesById(accommodationDetail.getAccommodationId()));

        log.info("✅ 숙소 상세 검색 완료 - result={}", accommodationDetail.getAccommodationName());
        log.info("숙소 상세 검색 완료 - result={}", accommodationDetail.getAccommodationImages());

        return accommodationDetail;
    }
}