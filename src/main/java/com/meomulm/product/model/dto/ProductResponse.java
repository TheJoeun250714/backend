package com.meomulm.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    // 객실 리스트
    private List<Product> allProducts;
    // 예약 가능한 아이디 리스트
    private List<Integer> availableProductIds;
}