package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    @Autowired private LikeService likeService;

    @PostMapping("/post/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.ok().build();
    }
}