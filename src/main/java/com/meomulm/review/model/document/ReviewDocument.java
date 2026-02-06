package com.meomulm.review.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 리뷰 Elasticsearch Document
 * Elasticsearch 8.x 기반
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "reviews")
public class ReviewDocument {

    @Id
    @Field(type = FieldType.Integer)
    private Integer reviewId;

    @Field(type = FieldType.Integer)
    private Integer accommodationId;

    @Field(type = FieldType.Integer)
    private Integer userId;

    @Field(type = FieldType.Text)
    private String userName;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String reviewContent;

    @Field(type = FieldType.Double)
    private Double rating;

    @Field(type = FieldType.Date)
    private String createdAt;

    @Field(type = FieldType.Date)
    private String updatedAt;
}
