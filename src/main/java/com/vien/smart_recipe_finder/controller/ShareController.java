package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares")
public class ShareController {
    @Autowired private ShareService shareService;

    @PostMapping("/post/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> sharePost(@PathVariable Long postId) {
        shareService.sharePost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unsharePost(@PathVariable Long postId) {
        shareService.unsharePost(postId);
        return ResponseEntity.noContent().build();
    }
}