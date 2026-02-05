package com.meomulm.review.model.service;

import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.review.model.dto.ReviewSummary;

import java.util.List;

public interface ReviewService {

    /**
     * 숙소별 리뷰 약식 조회
     * @param accommodationId 숙소 ID
     * @return
     */
    ReviewSummary getReviewSummaryByAccommodationId(int accommodationId);

    /**
     * 숙소별 리뷰 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 리뷰 DTO 리스트
     */
    List<AccommodationReview> getReviewByAccommodationId(int accommodationId);

    /**
     * 내 리뷰 조회
     * @param userId 유저 ID
     * @return 나의 리뷰 DTO 리스트
     */
    List<MyReview> getReviewByUserId(int userId);

    /**
     * 리뷰 작성
     * @param userId 유저 ID
     * @param accommodationId 숙소 ID
     * @param rating 별점
     * @param reviewContent 리뷰 내용
     */
    void postReview(int userId, int accommodationId, int rating, String reviewContent);

    /**
     * 리뷰 삭제
     * @param reviewId 리뷰 ID
     * @param userId 유저 ID
     */
    void deleteReview(int reviewId, int userId);
}
