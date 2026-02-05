package com.meomulm.review.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.review.model.dto.Review;
import com.meomulm.review.model.dto.ReviewSummary;
import com.meomulm.review.model.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthUtil authUtil;

    /**
     * 숙소별 리뷰 약식 조회
     * @param accommodationId
     * @return
     */
    @GetMapping("/summary/{accommodationId}")
    public ResponseEntity<ReviewSummary> getReviewSummary(@PathVariable int accommodationId) {
        ReviewSummary summary = reviewService.getReviewSummaryByAccommodationId(accommodationId);
        return ResponseEntity.ok(summary);
    }

    /**
     * 숙소별 리뷰 조회
     * @param accommodationId URL에서 가져온 숙소 ID
     * @return 숙소 리뷰 DTO 리스트 + 상태코드 200
     */
    @GetMapping("/accommodationId/{accommodationId}")
    public ResponseEntity<List<AccommodationReview>> getReviewByAccommodationId(@PathVariable int accommodationId) {
        List<AccommodationReview> reviews = reviewService.getReviewByAccommodationId(accommodationId);

        return ResponseEntity.ok(reviews);
    }

    /**
     * 내 리뷰 조회
     * @param authHeader JWT 토큰 헤더
     * @return 나의 리뷰 DTO 리스트 + 상태코드 200
     */
    // @GetMapping("/userId")
    @GetMapping
    public ResponseEntity<List<MyReview>> getReviewByUserId(@RequestHeader(value = "Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        List<MyReview> reviews = reviewService.getReviewByUserId(currentUserId);

        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 작성
     * @param authHeader JWT 토큰
     * @param review 리뷰 DTO
     * @return 상태코드 200
     */
    @PostMapping
    public ResponseEntity<Void> postReview(@RequestHeader(value = "Authorization") String authHeader, @RequestBody Review review) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        reviewService.postReview(
                currentUserId,
                review.getAccommodationId(),
                review.getRating(),
                review.getReviewContent()
        );

        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 삭제
     * @param authHeader JWT 토큰
     * @param reviewId URL에서 가져온 리뷰 ID
     * @return 상태코드 200
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@RequestHeader(value = "Authorization") String authHeader, @PathVariable int reviewId) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        reviewService.deleteReview(reviewId, currentUserId);

        return ResponseEntity.ok().build();
    }
}
