package com.meomulm.product.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * 객실/상품 Elasticsearch Document (2026 버전)
 * Elasticsearch 8.x 기반
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
public class ProductDocument {

    @Id
    @Field(type = FieldType.Integer)
    private Integer productId;

    @Field(type = FieldType.Integer)
    private Integer accommodationId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String productName;

    @Field(type = FieldType.Text)
    private String productDescription;

    @Field(type = FieldType.Integer)
    private Integer productPrice;

    @Field(type = FieldType.Integer)
    private Integer maxGuests;

    @Field(type = FieldType.Keyword)
    private List<String> facilities;

    @Field(type = FieldType.Text)
    private String mainImage;

    @Field(type = FieldType.Boolean)
    private Boolean isAvailable;

    @Field(type = FieldType.Date)
    private String createdAt;

    @Field(type = FieldType.Date)
    private String updatedAt;
}
