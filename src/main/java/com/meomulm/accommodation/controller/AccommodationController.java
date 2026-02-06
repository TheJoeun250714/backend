package com.meomulm.accommodation.controller;

import com.meomulm.accommodation.model.document.AccommodationDocument;
import com.meomulm.accommodation.model.dto.AccommodationDetail;
import com.meomulm.accommodation.model.dto.AccommodationImage;
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;
import com.meomulm.accommodation.model.service.AccommodationElasticsearchService;
import com.meomulm.accommodation.model.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 숙소 컨트롤러
 */
@RestController
@RequestMapping("/api/accommodation")
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    private final AccommodationService accommodationService;
    private final AccommodationElasticsearchService elasticsearchService;

    /**
     * 숙소ID로 숙소 이미지 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 이미지 DTO + 상태코드 200
     */
    @GetMapping("/{accommodationId}")
    public ResponseEntity<AccommodationImage> getAccommodationImage(@PathVariable int accommodationId) {
        log.info("Controller 진입 - accommodationId={}", accommodationId);
        AccommodationImage accommodationImage = accommodationService.getAccommodationImageById(accommodationId);
        log.info("숙소 이미지 조회 결과: {}", accommodationImage);
        return ResponseEntity.ok(accommodationImage);
    }

    /**
     * 통합 검색 (Elasticsearch 사용)
     * 키워드 / 현위치 / 필터링 통합 조회
     * @param request 검색 요청 DTO
     * @return 숙소검색 응답 DTO 리스트 + 상태코드 200
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchAccommodationResponse>> searchAccommodations(
            @ModelAttribute SearchAccommodationRequest request) {
        log.info("Elasticsearch 통합 검색 진입 - 파라미터: {}", request);

        // Elasticsearch로 검색
        List<AccommodationDocument> documents = elasticsearchService.searchAccommodations(request);

        // Document를 Response DTO로 변환
        List<SearchAccommodationResponse> results = documents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        log.info("Elasticsearch 검색 결과: {} 건", results.size());
        return ResponseEntity.ok(results);
    }

    /**
     * 키워드 자동완성 (Elasticsearch 사용)
     * @param prefix 검색어 접두사
     * @return 자동완성 결과 리스트
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String prefix) {
        log.info("자동완성 검색 - prefix={}", prefix);
        List<String> suggestions = elasticsearchService.autocomplete(prefix);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * 최근 본 숙소
     * @param ids 최근 본 숙소 아이디 리스트
     * @return 숙소검색 DTO 리스트
     */
    @PostMapping("/recent")
    public ResponseEntity<List<SearchAccommodationResponse>> getRecentAccommodations(
            @RequestBody List<Integer> ids
    ) {
        log.info("최근 본 숙소 조회 - ids={}", ids);
        List<SearchAccommodationResponse> results = accommodationService.getRecentAccommodations(ids);
        return ResponseEntity.ok(results);
    }

    /**
     * 지역별 인기 숙소 조회 (Elasticsearch 사용)
     * @param accommodationAddress 숙소 주소
     * @return 숙소검색 응답 DTO 리스트 + 상태코드 200
     */
    @GetMapping("/popular")
    public ResponseEntity<List<SearchAccommodationResponse>> getAccommodationPopularByAddress(
            @RequestParam String accommodationAddress) {
        log.info("Elasticsearch 인기 숙소 조회 - accommodationAddress={}", accommodationAddress);

        // Elasticsearch로 검색
        List<AccommodationDocument> documents = elasticsearchService.getPopularByAddress(accommodationAddress);

        // Document를 Response DTO로 변환
        List<SearchAccommodationResponse> results = documents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    /**
     * 현재위치 기반 반경 5km 내 숙소 조회 (Elasticsearch 사용)
     * @param request 숙소검색 요청 DTO
     * @return 숙소검색 응답 DTO 리스트 + 상태코드 200
     */
    @PostMapping("/map")
    public ResponseEntity<List<SearchAccommodationResponse>> searchByLocation(
            @RequestBody SearchAccommodationRequest request
    ) {
        log.info("Elasticsearch 위치 기반 검색 - location={},{}",
                request.getLatitude(), request.getLongitude());

        // Elasticsearch로 위치 기반 검색
        List<AccommodationDocument> documents = elasticsearchService.searchByLocation(
                request.getLatitude(),
                request.getLongitude()
        );

        // Document를 Response DTO로 변환
        List<SearchAccommodationResponse> results = documents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    /**
     * 숙소 ID로 숙소 상세정보 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 상세정보 DTO + 상태코드 200
     */
    @GetMapping("/detail/{accommodationId}")
    public ResponseEntity<AccommodationDetail> getAccommodationDetailById(
            @PathVariable int accommodationId) {
        log.info("Controller 진입 - accommodationId={}", accommodationId);
        AccommodationDetail accommodationDetail = accommodationService.getAccommodationDetailById(accommodationId);
        return ResponseEntity.ok(accommodationDetail);
    }

    /**
     * AccommodationDocument를 SearchAccommodationResponse로 변환
     */
    private SearchAccommodationResponse convertToResponse(AccommodationDocument doc) {
        SearchAccommodationResponse response = new SearchAccommodationResponse();
        response.setAccommodationId(doc.getAccommodationId());
        response.setAccommodationName(doc.getAccommodationName());
        response.setAccommodationAddress(doc.getAccommodationAddress());
        response.setAccommodationType(doc.getAccommodationType());
        response.setAccommodationLatitude(doc.getAccommodationLatitude());
        response.setAccommodationLongitude(doc.getAccommodationLongitude());
        response.setMinPrice(doc.getMinPrice());
        response.setAverageRating(doc.getAverageRating());
        response.setReviewCount(doc.getReviewCount());
        response.setMainImage(doc.getMainImage());
        return response;
    }
}
