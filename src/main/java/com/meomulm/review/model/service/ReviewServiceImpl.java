package com.meomulm.review.model.service;

import com.meomulm.common.exception.BadRequestException;
import com.meomulm.common.exception.NotFoundException;
import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.review.model.dto.Review;
import com.meomulm.review.model.dto.ReviewSummary;
import com.meomulm.review.model.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    @Override
    public ReviewSummary getReviewSummaryByAccommodationId(int accommodationId) {
        ReviewSummary summary = reviewMapper.selectReviewSummary(accommodationId);

        // 리뷰가 하나도 없는 경우 기본 객체 반환
        if (summary == null) {
            return new ReviewSummary(0.0, 0, "아직 작성된 리뷰가 없습니다.");
        }
        return summary;
    }

    /**
     * 숙소별 리뷰 조회
     * @param accommodationId 숙소 ID
     * @return 숙소 리뷰 DTO 리스트
     */
    @Override
    public List<AccommodationReview> getReviewByAccommodationId(int accommodationId) {
        List<AccommodationReview> reviews = reviewMapper.selectReviewByAccommodationId(accommodationId);

        if (reviews == null || reviews.isEmpty()) {
            throw new NotFoundException("해당 숙소의 리뷰가 존재하지 않습니다.");
        }

        return reviews;
    }

    /**
     * 내 리뷰 조회
     * @param userId 유저 ID
     * @return 나의 리뷰 DTO 리스트
     */
    @Override
    public List<MyReview> getReviewByUserId(int userId) {
        List<MyReview> reviews = reviewMapper.selectReviewByUserId(userId);

        if (reviews == null || reviews.isEmpty()) {
            throw new NotFoundException("작성한 리뷰가 없습니다.");
        }

        return reviews;
    }

    /**
     * 리뷰 작성
     * @param userId 유저 ID
     * @param accommodationId 숙소 ID
     * @param rating 별점
     * @param reviewContent 리뷰 내용
     */
    @Transactional
    @Override
    public void postReview(int userId, int accommodationId, int rating, String reviewContent) {

        if (rating < 1 || rating > 10) {
            throw new BadRequestException("평점은 1~10점 사이여야 합니다.");
        }

        if (reviewContent == null || reviewContent.trim().isEmpty()) {
            throw new BadRequestException("리뷰 내용을 입력해주세요.");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setAccommodationId(accommodationId);
        review.setRating(rating);
        review.setReviewContent(reviewContent);

        int result = reviewMapper.insertReview(review);

        if (result != 0) {
            throw new BadRequestException("리뷰 저장에 실패했습니다.");
        }
    }

    /**
     * 리뷰 삭제
     * @param reviewId 리뷰 ID
     * @param userId 유저 ID
     */
    @Transactional
    @Override
    public void deleteReview(int reviewId, int userId) {
        Review review = new Review();
        review.setReviewId(reviewId);
        review.setUserId(userId);

        int result = reviewMapper.deleteReview(review);

        if (result == 0) {
            throw new NotFoundException("삭제할 리뷰가 존재하지 않습니다.");
        }
    }

}
