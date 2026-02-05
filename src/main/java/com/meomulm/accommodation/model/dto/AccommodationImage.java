package com.meomulm.accommodation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationImage {
    // 숙소 이미지 아이디
    private int accommodationImageId;
    // 숙소 아이디
    private int accommodationId;
    // 숙소 이미지 URL
    private String accommodationImageUrl;
}
