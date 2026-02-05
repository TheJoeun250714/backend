package com.meomulm.product.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.product.model.dto.Product;
import com.meomulm.product.model.dto.ProductFacility;
import com.meomulm.product.model.dto.ProductImage;
import com.meomulm.product.model.dto.ProductResponse;
import com.meomulm.product.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.sql.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    /**
     * 숙소 ID로 객실 조회
     * @param accommodationId 숙소 ID
     * @param checkInDate 체크인 날짜
     * @param checkOutDate 체크아웃 날짜
     * @param guestCount 인원 수
     * @return 예약 가능한 객실 조회 결과 반환
     */
    @Override
    public ProductResponse getRoomsByAccommodationId(int accommodationId, String checkInDate, String checkOutDate, int guestCount) {

        if (accommodationId <= 0) {
            throw new BadRequestException("유효하지 않은 숙소 ID입니다.");
        }
        if (checkInDate == null || checkInDate.isBlank()
                || checkOutDate == null || checkOutDate.isBlank()) {
            throw new BadRequestException("체크인과 체크아웃 날짜는 필수입니다.");
        }
        if (checkInDate.equals(checkOutDate)) {
            throw new BadRequestException("체크인과 체크아웃 날짜는 같을 수 없습니다.");
        }
        if (guestCount <= 0) {
            throw new BadRequestException("인원수는 1명 이상이어야 합니다.");
        }
        List<Product> productList = productMapper.selectProductsByAccommodationId(accommodationId);
        if (productList == null || productList.isEmpty()) {
            throw new NotFoundException("해당 숙소에 등록된 객실이 없습니다.");
        }
        for (Product product : productList) {
            int productId = product.getProductId();
            ProductFacility facility = productMapper.selectProductFacilityByProductId(productId);
            product.setFacility(facility);
            List<ProductImage> images = productMapper.selectProductImagesByProductId(productId);
            if (images == null) {
                images = Collections.emptyList();
            }
            product.setImages(images);
        }
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);

        Date sqlCheckIn = Date.valueOf(checkIn);
        Date sqlCheckOut = Date.valueOf(checkOut);


        List<Integer> availableProductId = productMapper.selectAvailableProductId(
                accommodationId, sqlCheckIn, sqlCheckOut, guestCount);
        if (availableProductId == null) {
            availableProductId = Collections.emptyList();
        }
        return new ProductResponse(productList, availableProductId);
    }
}