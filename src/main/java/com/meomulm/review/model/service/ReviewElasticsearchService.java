package com.meomulm.review.model.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.meomulm.review.model.document.ReviewDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 Elasticsearch 검색 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 숙소별 리뷰 조회 (최신순)
     */
    public List<ReviewDocument> getReviewsByAccommodationId(Integer accommodationId) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("reviews")
                .query(q -> q
                    .term(t -> t
                        .field("accommodationId")
                        .value(accommodationId)
                    )
                )
                .sort(sort -> sort
                    .field(f -> f
                        .field("createdAt")
                        .order(SortOrder.Desc)
                    )
                )
                .size(100)
            );

            SearchResponse<ReviewDocument> response = elasticsearchClient.search(
                searchRequest,
                ReviewDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("리뷰 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 사용자별 리뷰 조회
     */
    public List<ReviewDocument> getReviewsByUserId(Integer userId) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("reviews")
                .query(q -> q
                    .term(t -> t
                        .field("userId")
                        .value(userId)
                    )
                )
                .sort(sort -> sort
                    .field(f -> f
                        .field("createdAt")
                        .order(SortOrder.Desc)
                    )
                )
                .size(100)
            );

            SearchResponse<ReviewDocument> response = elasticsearchClient.search(
                searchRequest,
                ReviewDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("사용자 리뷰 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 리뷰 내용으로 검색
     */
    public List<ReviewDocument> searchReviewsByKeyword(String keyword) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("reviews")
                .query(q -> q
                    .match(m -> m
                        .field("reviewContent")
                        .query(keyword)
                    )
                )
                .sort(sort -> sort
                    .field(f -> f
                        .field("createdAt")
                        .order(SortOrder.Desc)
                    )
                )
                .size(50)
            );

            SearchResponse<ReviewDocument> response = elasticsearchClient.search(
                searchRequest,
                ReviewDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("리뷰 검색 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 평점별 리뷰 필터링
     */
    public List<ReviewDocument> getReviewsByRating(Integer accommodationId, Double minRating) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("reviews")
                .query(q -> q
                    .bool(b -> b
                        .must(m -> m
                            .term(t -> t
                                .field("accommodationId")
                                .value(accommodationId)
                            )
                        )
                        .filter(f -> f
                            .range(r -> r
                                .field("rating")
                                    .gte(JsonData.of(minRating))
                            )
                        )
                    )
                )
                .sort(sort -> sort
                    .field(f -> f
                        .field("rating")
                        .order(SortOrder.Desc)
                    )
                )
                .size(100)
            );

            SearchResponse<ReviewDocument> response = elasticsearchClient.search(
                searchRequest,
                ReviewDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("평점별 리뷰 필터링 중 오류 발생", e);
            return new ArrayList<>();
        }
    }
}
