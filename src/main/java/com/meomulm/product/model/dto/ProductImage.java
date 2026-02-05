package com.meomulm.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {
    // 객실 이미지 아이디
    private int productImageId;
    // 객실 아이디
    private int productId;
    // 객실 이미지 URL
    private String productImageUrl;
}