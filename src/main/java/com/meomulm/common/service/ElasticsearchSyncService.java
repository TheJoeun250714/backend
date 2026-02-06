package com.meomulm.common.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.meomulm.accommodation.model.document.AccommodationDocument;
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest;
import com.meomulm.accommodation.model.dto.SearchAccommodationResponse;
import com.meomulm.accommodation.model.mapper.AccommodationMapper;
import com.meomulm.review.model.document.ReviewDocument;
import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL과 Elasticsearch 간 데이터 동기화 서비스 (2026 버전)
 * 실시간 동기화 및 배치 동기화 지원
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchSyncService {

    private final ElasticsearchClient elasticsearchClient;
    private final AccommodationMapper accommodationMapper;
    private final ReviewMapper reviewMapper;

    /**
     * 숙소 데이터 실시간 동기화
     */
    public void syncAccommodation(Integer accommodationId) {
        try {
            // PostgreSQL에서 데이터 조회 - 기존 메서드 활용
            List<Integer> ids = new ArrayList<>();
            ids.add(accommodationId);
            List<SearchAccommodationResponse> results = accommodationMapper.selectRecentAccommodations(ids);

            if (results == null || results.isEmpty()) {
                log.warn("숙소 ID {}를 찾을 수 없습니다.", accommodationId);
                return;
            }

            SearchAccommodationResponse accommodation = results.get(0);

            // Elasticsearch Document 변환
            AccommodationDocument document = convertToDocument(accommodation);

            // Elasticsearch에 인덱싱
            elasticsearchClient.index(i -> i
                    .index("accommodations")
                    .id(String.valueOf(accommodationId))
                    .document(document)
            );

            log.info("숙소 ID {} 동기화 완료", accommodationId);

        } catch (IOException e) {
            log.error("숙소 동기화 중 오류 발생: {}", accommodationId, e);
        }
    }

    /**
     * 리뷰 데이터 실시간 동기화
     */
    public void syncReview(Integer reviewId) {
        try {
            // PostgreSQL에서 데이터 조회 - 기존 메서드 활용
            // reviewId로 직접 조회하는 메서드가 없으므로, 해당 리뷰가 속한 숙소의 모든 리뷰를 조회한 후 필터링
            // 또는 간단하게 스킵 (실시간 동기화는 배치에서 처리)
            log.warn("리뷰 ID {} 실시간 동기화는 배치 동기화에서 처리됩니다.", reviewId);

        } catch (Exception e) {
            log.error("리뷰 동기화 중 오류 발생: {}", reviewId, e);
        }
    }

    /**
     * 전체 숙소 데이터 배치 동기화 (매일 새벽 3시 실행)
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void syncAllAccommodations() {
        log.info("전체 숙소 배치 동기화 시작");

        try {
            // PostgreSQL에서 전체 숙소 조회 - 기존 메서드 활용 (빈 조건으로 전체 조회)
            SearchAccommodationRequest request = new SearchAccommodationRequest();
            List<SearchAccommodationResponse> accommodations = accommodationMapper.selectAccommodations(request);

            if (accommodations == null || accommodations.isEmpty()) {
                log.warn("동기화할 숙소 데이터가 없습니다.");
                return;
            }

            // Bulk 작업 생성
            List<BulkOperation> operations = new ArrayList<>();

            for (SearchAccommodationResponse accommodation : accommodations) {
                AccommodationDocument document = convertToDocument(accommodation);

                operations.add(BulkOperation.of(b -> b
                        .index(idx -> idx
                                .index("accommodations")
                                .id(String.valueOf(accommodation.getAccommodationId()))
                                .document(document)
                        )
                ));
            }

            // Bulk 요청 실행
            BulkRequest bulkRequest = BulkRequest.of(b -> b
                    .operations(operations)
            );

            BulkResponse response = elasticsearchClient.bulk(bulkRequest);

            if (response.errors()) {
                log.error("배치 동기화 중 일부 오류 발생");
            } else {
                log.info("전체 숙소 배치 동기화 완료: {} 건", accommodations.size());
            }

        } catch (IOException e) {
            log.error("전체 숙소 배치 동기화 중 오류 발생", e);
        }
    }

    /**
     * 전체 리뷰 데이터 배치 동기화 (매일 새벽 4시 실행)
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void syncAllReviews() {
        log.info("전체 리뷰 배치 동기화 시작");

        try {
            // 전체 숙소 조회
            SearchAccommodationRequest request = new SearchAccommodationRequest();
            List<SearchAccommodationResponse> accommodations = accommodationMapper.selectAccommodations(request);

            if (accommodations == null || accommodations.isEmpty()) {
                log.warn("동기화할 숙소가 없습니다.");
                return;
            }

            List<BulkOperation> operations = new ArrayList<>();

            // 각 숙소별로 리뷰 조회 후 동기화
            for (SearchAccommodationResponse accommodation : accommodations) {
                List<AccommodationReview> reviews = reviewMapper.selectReviewByAccommodationId(
                        accommodation.getAccommodationId()
                );

                if (reviews != null && !reviews.isEmpty()) {
                    for (AccommodationReview review : reviews) {
                        ReviewDocument document = convertToReviewDocument(review);

                        operations.add(BulkOperation.of(b -> b
                                .index(idx -> idx
                                        .index("reviews")
                                        .id(String.valueOf(review.getReviewId()))
                                        .document(document)
                                )
                        ));
                    }
                }
            }

            if (operations.isEmpty()) {
                log.warn("동기화할 리뷰 데이터가 없습니다.");
                return;
            }

            // Bulk 요청 실행
            BulkRequest bulkRequest = BulkRequest.of(b -> b
                    .operations(operations)
            );

            BulkResponse response = elasticsearchClient.bulk(bulkRequest);

            if (response.errors()) {
                log.error("리뷰 배치 동기화 중 일부 오류 발생");
            } else {
                log.info("전체 리뷰 배치 동기화 완료: {} 건", operations.size());
            }

        } catch (IOException e) {
            log.error("전체 리뷰 배치 동기화 중 오류 발생", e);
        }
    }

    /**
     * 숙소 데이터 삭제 동기화
     */
    public void deleteAccommodation(Integer accommodationId) {
        try {
            elasticsearchClient.delete(d -> d
                    .index("accommodations")
                    .id(String.valueOf(accommodationId))
            );

            log.info("숙소 ID {} 삭제 동기화 완료", accommodationId);

        } catch (IOException e) {
            log.error("숙소 삭제 동기화 중 오류 발생: {}", accommodationId, e);
        }
    }

    /**
     * 리뷰 데이터 삭제 동기화
     */
    public void deleteReview(Integer reviewId) {
        try {
            elasticsearchClient.delete(d -> d
                    .index("reviews")
                    .id(String.valueOf(reviewId))
            );

            log.info("리뷰 ID {} 삭제 동기화 완료", reviewId);

        } catch (IOException e) {
            log.error("리뷰 삭제 동기화 중 오류 발생: {}", reviewId, e);
        }
    }

    /**
     * DTO를 Elasticsearch Document로 변환
     */
    private AccommodationDocument convertToDocument(SearchAccommodationResponse accommodation) {
        AccommodationDocument document = AccommodationDocument.builder()
                .accommodationId(accommodation.getAccommodationId())
                .accommodationName(accommodation.getAccommodationName())
                .accommodationAddress(accommodation.getAccommodationAddress())
                .accommodationType(accommodation.getAccommodationType())
                .accommodationLatitude(accommodation.getAccommodationLatitude())
                .accommodationLongitude(accommodation.getAccommodationLongitude())
                .minPrice(accommodation.getMinPrice())
                .averageRating(accommodation.getAverageRating())
                .reviewCount(accommodation.getReviewCount())
                .mainImage(accommodation.getMainImage())
                .build();

        // GeoPoint 설정
        document.setLocationFromCoordinates(
                accommodation.getAccommodationLatitude(),
                accommodation.getAccommodationLongitude()
        );

        return document;
    }

    /**
     * 리뷰 DTO를 Elasticsearch Document로 변환
     */
    private ReviewDocument convertToReviewDocument(AccommodationReview review) {
        return ReviewDocument.builder()
                .reviewId(review.getReviewId())
                .accommodationId(review.getAccommodationId())
                .userId(review.getUserId())
                .userName(review.getUserName())
                .reviewContent(review.getReviewContent())
                .rating((double) review.getRating())  // int -> Double 변환
                .createdAt(review.getCreatedAt())
                .build();
    }
}