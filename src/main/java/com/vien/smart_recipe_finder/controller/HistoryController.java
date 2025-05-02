package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.dto.HistoryDTO;
import com.vien.smart_recipe_finder.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @PostMapping("/{recipeId}")
    public ResponseEntity<HistoryDTO> saveHistory(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @PathVariable Long recipeId) {
        String email = oAuth2User.getAttribute("email");
        Long userId = historyService.getUserIdByEmail(email); // Giả định HistoryService có phương thức này
        HistoryDTO history = historyService.saveHistory(userId, recipeId);
        return ResponseEntity.ok(history);
    }

    @GetMapping
    public ResponseEntity<Page<HistoryDTO>> getUserHistory(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {
        String email = oAuth2User.getAttribute("email");
        Long userId = historyService.getUserIdByEmail(email);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<HistoryDTO> historyPage = historyService.getUserHistory(userId, pageable);
        return ResponseEntity.ok(historyPage);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteHistory(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @PathVariable Long recipeId) {
        String email = oAuth2User.getAttribute("email");
        Long userId = historyService.getUserIdByEmail(email);
        historyService.deleteHistory(userId, recipeId);
        return ResponseEntity.noContent().build();
    }
}