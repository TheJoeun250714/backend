package com.meomulm.accommodation.model.repository;

import com.meomulm.accommodation.model.document.AccommodationDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 숙소 Elasticsearch Repository
 * Spring Data Elasticsearch 기반
 */
@Repository
public interface AccommodationElasticsearchRepository 
        extends ElasticsearchRepository<AccommodationDocument, Integer> {

    /**
     * 숙소명 또는 주소로 검색 (한글 분석기 적용)
     */
    @Query("{\"bool\": {\"should\": [" +
           "{\"match\": {\"accommodationName\": {\"query\": \"?0\", \"boost\": 2}}}," +
           "{\"match\": {\"accommodationAddress\": \"?0\"}}" +
           "]}}")
    List<AccommodationDocument> searchByKeyword(String keyword);

    /**
     * 숙소 타입으로 필터링
     */
    List<AccommodationDocument> findByAccommodationType(String type);

    /**
     * 가격 범위로 필터링
     */
    List<AccommodationDocument> findByMinPriceBetween(Integer minPrice, Integer maxPrice);

    /**
     * 평점 이상으로 필터링
     */
    List<AccommodationDocument> findByAverageRatingGreaterThanEqual(Double rating);
}
