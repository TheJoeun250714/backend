package com.meomulm.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyReview {
    // 리뷰 아이디
    private int reviewId;
    // 유저 아이디
    private int userId;
    // 숙소 아이디
    private int accommodationId;
    // 평점
    private int rating;
    // 리뷰 내용
    private String reviewContent;
    // 리뷰 작성일자
    private String createdAt;

    // 숙소 이름
    private String accommodationName;
}
