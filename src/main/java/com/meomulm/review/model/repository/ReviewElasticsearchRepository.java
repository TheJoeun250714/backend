package com.meomulm.review.model.repository;

import com.meomulm.review.model.document.ReviewDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 리뷰 Elasticsearch Repository
 * Spring Data Elasticsearch 기반
 */
@Repository
public interface ReviewElasticsearchRepository 
        extends ElasticsearchRepository<ReviewDocument, Integer> {

    /**
     * 숙소 ID로 리뷰 조회
     */
    List<ReviewDocument> findByAccommodationId(Integer accommodationId);

    /**
     * 사용자 ID로 리뷰 조회
     */
    List<ReviewDocument> findByUserId(Integer userId);

    /**
     * 리뷰 내용으로 검색 (한글 분석기 적용)
     */
    @Query("{\"match\": {\"reviewContent\": \"?0\"}}")
    List<ReviewDocument> searchByContent(String keyword);

    /**
     * 평점 범위로 필터링
     */
    List<ReviewDocument> findByRatingBetween(Double minRating, Double maxRating);
}
