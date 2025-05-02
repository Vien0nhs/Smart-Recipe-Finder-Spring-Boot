package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.dto.CommentDTO;
import com.vien.smart_recipe_finder.dto.RatingDTO;
import com.vien.smart_recipe_finder.service.OAuth2UserService;
import com.vien.smart_recipe_finder.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;
    @Autowired
    private OAuth2UserService oAuth2UserService;
    @PostMapping("/init")
    public ResponseEntity<Void> initXsrfToken() {
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{recipeId}")
    public ResponseEntity<RatingDTO> createOrUpdateRating(
            @PathVariable Long recipeId,
            @RequestBody RatingDTO ratingDTO,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(403).body(null);
        }
        System.out.println("OAuth2User: " + oAuth2User);
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(403).body(null);
        }
        Long userId = getUserIdFromEmail(email);
        ratingDTO.setUserId(userId);
        ratingDTO.setRecipeId(recipeId);
        RatingDTO result = ratingService.createOrUpdateRating(userId, recipeId, ratingDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{recipeId}/user")
    public ResponseEntity<RatingDTO> getUserRating(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        Long userId = getUserIdFromEmail(email);
        RatingDTO rating = ratingService.getUserRating(userId, recipeId);
        return rating != null ? ResponseEntity.ok(rating) : ResponseEntity.noContent().build();
    }

    @GetMapping("/{recipeId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long recipeId) {
        Double avg = ratingService.getAverageRating(recipeId);
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/{recipeId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long recipeId) {
        List<CommentDTO> comments = ratingService.getComments(recipeId);
        return ResponseEntity.ok(comments);
    }
    @DeleteMapping("/{recipeId}/comment")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null || oAuth2User.getAttribute("email") == null) {
            return ResponseEntity.status(403).build();
        }
        Long userId = getUserIdFromEmail(oAuth2User.getAttribute("email"));
        ratingService.deleteComment(userId, recipeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{recipeId}/comment")
    public ResponseEntity<RatingDTO> updateComment(
            @PathVariable Long recipeId,
            @RequestBody String comment,
            @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null || oAuth2User.getAttribute("email") == null) {
            return ResponseEntity.status(403).body(null);
        }
        Long userId = getUserIdFromEmail(oAuth2User.getAttribute("email"));
        RatingDTO result = ratingService.updateComment(userId, recipeId, comment);
        return ResponseEntity.ok(result);
    }
    private Long getUserIdFromEmail(String email) {
        return oAuth2UserService.getUserIdByEmail(email);
    }
}