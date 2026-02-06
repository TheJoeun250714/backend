package com.meomulm.accommodation.model.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.JsonNode;
import com.meomulm.accommodation.model.document.AccommodationDocument;
import com.meomulm.accommodation.model.dto.SearchAccommodationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 검색 서비스 (2026 버전)
 * Elasticsearch Java API Client 8.x 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccommodationElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 통합 검색 (키워드, 위치, 필터링)
     */
    public List<AccommodationDocument> searchAccommodations(SearchAccommodationRequest request) {
        try {
            // 쿼리 빌더
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();

            // 1. 키워드 검색 (숙소명, 주소)
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                boolQuery.should(s -> s
                    .match(m -> m
                        .field("accommodationName")
                        .query(request.getKeyword())
                        .boost(2.0f)
                    )
                );
                boolQuery.should(s -> s
                    .match(m -> m
                        .field("accommodationAddress")
                        .query(request.getKeyword())
                    )
                );
                boolQuery.minimumShouldMatch("1");
            }

            // 2. 위치 기반 검색 (반경 5km)
            if (request.getLatitude() != null && request.getLongitude() != null) {
                boolQuery.filter(f -> f
                    .geoDistance(g -> g
                        .field("location")
                        .distance("5km")
                        .location(l -> l
                            .latlon(ll -> ll
                                .lat(request.getLatitude())
                                .lon(request.getLongitude())
                            )
                        )
                    )
                );
            }

            // 3. 숙소 타입 필터
            if (request.getAccommodationType() != null && !request.getAccommodationType().isEmpty()) {
                boolQuery.filter(f -> f
                    .term(t -> t
                        .field("accommodationType")
                        .value(request.getAccommodationType())
                    )
                );
            }

            // 4. 가격 범위 필터
            if (request.getMinPrice() != null || request.getMaxPrice() != null) {
                RangeQuery.Builder rangeBuilder = new RangeQuery.Builder().field("minPrice");

                if (request.getMinPrice() != null) {
                    rangeBuilder.gte(JsonData.of(request.getMinPrice()));
                }
                if (request.getMaxPrice() != null) {
                    rangeBuilder.lte(JsonData.of(request.getMaxPrice()));
                }

                boolQuery.filter(f -> f.range(rangeBuilder.build()));
            }

            // 5. 평점 필터
            if (request.getMinRating() != null) {
                boolQuery.filter(f -> f
                    .range(r -> r
                        .field("averageRating")
                            .gte(JsonData.of(request.getMinRating()))
                    )
                );
            }

            // 검색 요청 빌드
            SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                .index("accommodations")
                .query(q -> q.bool(boolQuery.build()));

            // 정렬
            if (request.getSortBy() != null) {
                switch (request.getSortBy()) {
                    case "price_asc":
                        searchBuilder.sort(s -> s.field(f -> f.field("minPrice").order(SortOrder.Asc)));
                        break;
                    case "price_desc":
                        searchBuilder.sort(s -> s.field(f -> f.field("minPrice").order(SortOrder.Desc)));
                        break;
                    case "rating":
                        searchBuilder.sort(s -> s.field(f -> f.field("averageRating").order(SortOrder.Desc)));
                        break;
                    case "review_count":
                        searchBuilder.sort(s -> s.field(f -> f.field("reviewCount").order(SortOrder.Desc)));
                        break;
                }
            }

            // 페이징
            int from = request.getPage() != null ? (request.getPage() - 1) * 20 : 0;
            searchBuilder.from(from).size(20);

            // 검색 실행
            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchBuilder.build(),
                AccommodationDocument.class
            );

            // 결과 변환
            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Elasticsearch 검색 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 위치 기반 검색 (반경 5km)
     */
    public List<AccommodationDocument> searchByLocation(Double latitude, Double longitude) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("accommodations")
                .query(q -> q
                    .geoDistance(g -> g
                        .field("location")
                        .distance("5km")
                        .location(l -> l
                            .latlon(ll -> ll
                                .lat(latitude)
                                .lon(longitude)
                            )
                        )
                    )
                )
                .sort(sort -> sort
                    .geoDistance(g -> g
                        .field("location")
                        .location(l -> l
                            .latlon(ll -> ll
                                .lat(latitude)
                                .lon(longitude)
                            )
                        )
                        .order(SortOrder.Asc)
                    )
                )
                .size(50)
            );

            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchRequest,
                AccommodationDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("위치 기반 검색 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 키워드 자동완성
     */
    public List<String> autocomplete(String prefix) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("accommodations")
                .query(q -> q
                    .multiMatch(m -> m
                        .fields("accommodationName", "accommodationAddress")
                        .query(prefix)
                        .type(TextQueryType.PhrasePrefix)
                    )
                )
                .size(10)
            );

            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchRequest,
                AccommodationDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .map(AccommodationDocument::getAccommodationName)
                .distinct()
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("자동완성 검색 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 인기 숙소 조회 (지역별 가격 낮은 순)
     */
    public List<AccommodationDocument> getPopularByAddress(String address) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("accommodations")
                .query(q -> q
                    .match(m -> m
                        .field("accommodationAddress")
                        .query(address)
                    )
                )
                .sort(sort -> sort
                    .field(f -> f
                        .field("minPrice")
                        .order(SortOrder.Asc)
                    )
                )
                .size(12)
            );

            SearchResponse<AccommodationDocument> response = elasticsearchClient.search(
                searchRequest,
                AccommodationDocument.class
            );

            return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("인기 숙소 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }
}
