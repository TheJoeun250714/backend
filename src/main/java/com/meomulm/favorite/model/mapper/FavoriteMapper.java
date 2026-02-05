package com.meomulm.favorite.model.mapper;


import com.meomulm.favorite.model.dto.Favorite;
import com.meomulm.favorite.model.dto.SelectFavorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    // 유저 아이디를 기반으로 찜 리스트 조회
    List<SelectFavorite> selectAllFavorites(int userId);

    Integer selectFavorite(int userId, int accommodationId);
    // 찜 목록 추가
    void insertFavorite(Favorite favorite);

    // 찜 목록 삭제
    void deleteFavorite(int userId, int favoriteId);
}