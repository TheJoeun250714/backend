package com.meomulm.product.model.mapper;


import com.meomulm.product.model.dto.Product;
import com.meomulm.product.model.dto.ProductFacility;
import com.meomulm.product.model.dto.ProductImage;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface ProductMapper {
    // 숙소 아이디를 기반으로 객실 리스트 조회
    List<Product> selectProductsByAccommodationId(int accommodationId);

    // 객실 아이디를 기반으로 객실별 편의시설 조회
    ProductFacility selectProductFacilityByProductId(int productId);

    // 객실 아이디를 기반으로 객실별 이미지 조회
    List<ProductImage> selectProductImagesByProductId(int productId);

    // 예약 가능 객실 조회
    List<Integer> selectAvailableProductId(int accommodationId, Date checkInDate, Date checkOutDate, int guestCount);
}