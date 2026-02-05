package com.meomulm.favorite.model.service;

import com.meomulm.common.exception.NotFoundException;
import com.meomulm.favorite.model.dto.Favorite;
import com.meomulm.favorite.model.dto.SelectFavorite;
import com.meomulm.favorite.model.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteMapper favoriteMapper;

    /**
     * 사용자 찜 목록 가져오기
     * @param userId 유저 ID
     * @return 찜 목록 조회 DTO 리스트
     */
    @Override
    public List<SelectFavorite> getAllFavorites(int userId){
            List<SelectFavorite> favorites = favoriteMapper.selectAllFavorites(userId);
            log.info("서비스에서 가져온 데이터들 : {}", favorites);
        if (favorites == null || favorites.isEmpty()) {
            log.info("서비스에서 가져온 데이터들 : {}", favorites);
            throw new NotFoundException("사용자의 즐겨찾기 목록이 없습니다.");
        }
            return favorites;
    }

    @Override
    public Integer selectFavorite(int userId, int accommodationId) {
        return favoriteMapper.selectFavorite(userId, accommodationId);
    }

    /**
     * 사용자 찜 추가하기
     * @param userId 유저 ID
     * @param accommodationId 숙소 ID
     */
    @Transactional
    @Override
    public void postFavorite(int userId, int accommodationId){
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setAccommodationId(accommodationId);
            favoriteMapper.insertFavorite(favorite);
    }

    /**
     * 사용자 찜 삭제하기
     * @param userId 유저 ID
     * @param favoriteId 찜 ID
     */
    @Transactional
    @Override
    public void deleteFavorite(int userId, int favoriteId){
            favoriteMapper.deleteFavorite(userId, favoriteId);
    }
}
