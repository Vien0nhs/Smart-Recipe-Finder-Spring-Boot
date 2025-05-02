package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.dto.CommentPostDTO;
import com.vien.smart_recipe_finder.service.CommentPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired private CommentPostService commentService;

    @PostMapping("/post/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentPostDTO> createComment(
            @PathVariable Long postId,
            @RequestParam String content) {
        CommentPostDTO comment = commentService.createComment(postId, content);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentPostDTO> updateComment(
            @PathVariable Long commentId,
            @RequestParam String content) {
        CommentPostDTO comment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentPostDTO>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getComments(postId, page, size));
    }
}