package com.meomulm.accommodation.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

/**
 * 숙소 Elasticsearch Document
 * Elasticsearch 8.x 기반
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "accommodations")
public class AccommodationDocument {

    @Id
    @Field(type = FieldType.Integer)
    private Integer accommodationId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String accommodationName;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String accommodationAddress;

    @Field(type = FieldType.Keyword)
    private String accommodationType;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Double)
    private Double accommodationLatitude;

    @Field(type = FieldType.Double)
    private Double accommodationLongitude;

    @Field(type = FieldType.Integer)
    private Integer minPrice;

    @Field(type = FieldType.Double)
    private Double averageRating;

    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    @Field(type = FieldType.Text)
    private String mainImage;

    @Field(type = FieldType.Keyword)
    private List<String> facilities;

    @Field(type = FieldType.Date)
    private String createdAt;

    @Field(type = FieldType.Date)
    private String updatedAt;

    /**
     * GeoPoint 설정 헬퍼 메서드
     */
    public void setLocationFromCoordinates(Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            this.location = new GeoPoint(latitude, longitude);
            this.accommodationLatitude = latitude;
            this.accommodationLongitude = longitude;
        }
    }
}
