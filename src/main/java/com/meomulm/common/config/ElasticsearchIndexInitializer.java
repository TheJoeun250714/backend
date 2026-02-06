package com.meomulm.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.StringReader;

/**
 * Elasticsearch 인덱스 초기화 클래스 (2026 버전)
 * 애플리케이션 시작 시 필요한 인덱스 생성 및 매핑 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void run(String... args) throws Exception {
        createAccommodationIndex();
        createReviewIndex();
        createProductIndex();
    }

    /**
     * 숙소 인덱스 생성
     */
    private void createAccommodationIndex() {
        try {
            String indexName = "accommodations";
            
            // 인덱스 존재 여부 확인
            BooleanResponse exists = elasticsearchClient.indices().exists(
                ExistsRequest.of(e -> e.index(indexName))
            );

            if (exists.value()) {
                log.info("인덱스 '{}' 가 이미 존재합니다.", indexName);
                return;
            }

            // 인덱스 매핑 설정 (JSON)
            String mapping = """
                {
                  "properties": {
                    "accommodationId": { "type": "integer" },
                    "accommodationName": { 
                      "type": "text",
                      "analyzer": "nori",
                      "fields": {
                        "keyword": { "type": "keyword" }
                      }
                    },
                    "accommodationAddress": { 
                      "type": "text",
                      "analyzer": "nori"
                    },
                    "accommodationType": { "type": "keyword" },
                    "location": { "type": "geo_point" },
                    "accommodationLatitude": { "type": "double" },
                    "accommodationLongitude": { "type": "double" },
                    "minPrice": { "type": "integer" },
                    "averageRating": { "type": "double" },
                    "reviewCount": { "type": "integer" },
                    "mainImage": { "type": "text" },
                    "facilities": { "type": "keyword" },
                    "createdAt": { "type": "date" },
                    "updatedAt": { "type": "date" }
                  }
                }
                """;

            // 인덱스 설정 (한글 분석기 nori 플러그인 사용)
            String settings = """
                {
                  "analysis": {
                    "analyzer": {
                      "nori": {
                        "type": "custom",
                        "tokenizer": "nori_tokenizer",
                        "filter": ["lowercase", "nori_part_of_speech"]
                      }
                    }
                  }
                }
                """;

            // 인덱스 생성
            elasticsearchClient.indices().create(c -> c
                .index(indexName)
                .settings(s -> s.withJson(new StringReader(settings)))
                .mappings(m -> m.withJson(new StringReader(mapping)))
            );

            log.info("인덱스 '{}' 생성 완료", indexName);

        } catch (Exception e) {
            log.error("숙소 인덱스 생성 중 오류 발생", e);
        }
    }

    /**
     * 리뷰 인덱스 생성
     */
    private void createReviewIndex() {
        try {
            String indexName = "reviews";
            
            BooleanResponse exists = elasticsearchClient.indices().exists(
                ExistsRequest.of(e -> e.index(indexName))
            );

            if (exists.value()) {
                log.info("인덱스 '{}' 가 이미 존재합니다.", indexName);
                return;
            }

            String mapping = """
                {
                  "properties": {
                    "reviewId": { "type": "integer" },
                    "accommodationId": { "type": "integer" },
                    "userId": { "type": "integer" },
                    "userName": { "type": "text" },
                    "reviewContent": { 
                      "type": "text",
                      "analyzer": "nori"
                    },
                    "rating": { "type": "double" },
                    "createdAt": { "type": "date" },
                    "updatedAt": { "type": "date" }
                  }
                }
                """;

            String settings = """
                {
                  "analysis": {
                    "analyzer": {
                      "nori": {
                        "type": "custom",
                        "tokenizer": "nori_tokenizer",
                        "filter": ["lowercase", "nori_part_of_speech"]
                      }
                    }
                  }
                }
                """;

            elasticsearchClient.indices().create(c -> c
                .index(indexName)
                .settings(s -> s.withJson(new StringReader(settings)))
                .mappings(m -> m.withJson(new StringReader(mapping)))
            );

            log.info("인덱스 '{}' 생성 완료", indexName);

        } catch (Exception e) {
            log.error("리뷰 인덱스 생성 중 오류 발생", e);
        }
    }

    /**
     * 상품(객실) 인덱스 생성
     */
    private void createProductIndex() {
        try {
            String indexName = "products";
            
            BooleanResponse exists = elasticsearchClient.indices().exists(
                ExistsRequest.of(e -> e.index(indexName))
            );

            if (exists.value()) {
                log.info("인덱스 '{}' 가 이미 존재합니다.", indexName);
                return;
            }

            String mapping = """
                {
                  "properties": {
                    "productId": { "type": "integer" },
                    "accommodationId": { "type": "integer" },
                    "productName": { 
                      "type": "text",
                      "analyzer": "nori"
                    },
                    "productDescription": { 
                      "type": "text",
                      "analyzer": "nori"
                    },
                    "productPrice": { "type": "integer" },
                    "maxGuests": { "type": "integer" },
                    "facilities": { "type": "keyword" },
                    "mainImage": { "type": "text" },
                    "isAvailable": { "type": "boolean" },
                    "createdAt": { "type": "date" },
                    "updatedAt": { "type": "date" }
                  }
                }
                """;

            String settings = """
                {
                  "analysis": {
                    "analyzer": {
                      "nori": {
                        "type": "custom",
                        "tokenizer": "nori_tokenizer",
                        "filter": ["lowercase", "nori_part_of_speech"]
                      }
                    }
                  }
                }
                """;

            elasticsearchClient.indices().create(c -> c
                .index(indexName)
                .settings(s -> s.withJson(new StringReader(settings)))
                .mappings(m -> m.withJson(new StringReader(mapping)))
            );

            log.info("인덱스 '{}' 생성 완료", indexName);

        } catch (Exception e) {
            log.error("상품 인덱스 생성 중 오류 발생", e);
        }
    }
}
