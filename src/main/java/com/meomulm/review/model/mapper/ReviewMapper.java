package com.meomulm.review.model.mapper;


import com.meomulm.review.model.dto.AccommodationReview;
import com.meomulm.review.model.dto.MyReview;
import com.meomulm.review.model.dto.Review;
import com.meomulm.review.model.dto.ReviewSummary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {

    // 숙소아이디를 기반으로 리뷰 약식 조회
    ReviewSummary selectReviewSummary(int accommodationId);
    // 숙소아이디를 기반으로 리뷰 조회
    List<AccommodationReview> selectReviewByAccommodationId(int accommodationId);
    // 유저아이디를 기반으로 리뷰 조회
    List<MyReview> selectReviewByUserId(int userId);
    // 리뷰 추가
    int insertReview(Review review);
    // 리뷰 삭제
    int deleteReview(Review review);

}
