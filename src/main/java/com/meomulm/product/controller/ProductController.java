package com.meomulm.product.controller;

import com.meomulm.product.model.dto.ProductResponse;
import com.meomulm.product.model.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 숙소 ID로 객실 조회
     * @param accommodationId   숙소 Id
     * @param checkInDate       체크인 날짜
     * @param checkOutDate      체크아웃 날짜
     * @param guestCount        인원 수
     * @return                  예약 가능한 객실 조회 결과 반환
     */
    @GetMapping("/search/{accommodationId}")
    public ResponseEntity<ProductResponse> getRoomsByAccommodationId(
            @PathVariable int accommodationId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(required = false, defaultValue = "2") int guestCount){
        ProductResponse res = productService.getRoomsByAccommodationId(
                accommodationId, checkInDate, checkOutDate, guestCount);
        return ResponseEntity.ok(res);
    }
}