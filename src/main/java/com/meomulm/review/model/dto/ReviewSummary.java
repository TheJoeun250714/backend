package com.meomulm.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummary {
    private double averageRating;  // 평균 평점
    private int totalCount;        // 총 리뷰 개수
    private String latestContent;  // 가장 최근 리뷰 내용
}