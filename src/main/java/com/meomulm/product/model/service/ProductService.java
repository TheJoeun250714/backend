package com.meomulm.product.model.service;

import com.meomulm.product.model.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public interface ProductService {
    /**
     * 숙소 ID로 객실 조회
     * @param accommodationId 숙소 ID
     * @param checkInDate 체크인 날짜
     * @param checkOutDate 체크아웃 날짜
     * @param guestCount 인원 수
     * @return 예약 가능한 객실 조회 결과 반환
     */
    ProductResponse getRoomsByAccommodationId(int accommodationId, String checkInDate, String checkOutDate, int guestCount);
}