package com.meomulm.favorite.controller;

import com.meomulm.common.util.AuthUtil;
import com.meomulm.favorite.model.dto.SelectFavorite;
import com.meomulm.favorite.model.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthUtil authUtil;

    /**
     * 사용자 찜 목록 가져오기
     * @param authHeader JWT 토큰
     * @return 찜 목록 조회 DTO 리스트 + 상태코드 200
     */
    @GetMapping
    public ResponseEntity<List<SelectFavorite>> getAllFavorites(@RequestHeader("Authorization") String authHeader) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        List<SelectFavorite> favorites = favoriteService.getAllFavorites(currentUserId);
        return ResponseEntity.ok(favorites);
    }


    @GetMapping("/accommodation")
    public ResponseEntity<Integer> getFavorite(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int accommodationId) {

        int currentUserId = authUtil.getCurrentUserId(authHeader);

        // 특정 숙소 찜 여부 조회
        Integer favoriteId = favoriteService.selectFavorite(currentUserId, accommodationId);

        return ResponseEntity.ok(favoriteId != null ? favoriteId : 0);
    }


    /**
     * 사용자 찜 추가하기
     * @param accommodationId 숙소 ID
     * @param authHeader      JWT 토큰
     * @return 성공 메세지 + 상태코드 200
     */
    @PostMapping("/{accommodationId}")
    public ResponseEntity<Void> postFavorite(@RequestHeader("Authorization") String authHeader, @PathVariable int accommodationId) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        favoriteService.postFavorite(currentUserId, accommodationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자 찜 삭제하기
     * @param favoriteId 찜 ID
     * @param authHeader JWT 토큰
     * @return 성공 메세지 + 상태코드 200
     */
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@RequestHeader("Authorization") String authHeader, @PathVariable int favoriteId) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);

        favoriteService.deleteFavorite(currentUserId, favoriteId);
        return ResponseEntity.ok().build();

    }
}
