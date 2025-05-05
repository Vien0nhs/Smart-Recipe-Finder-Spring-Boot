package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.dto.CreatePostRequest;
import com.vien.smart_recipe_finder.dto.PostDTO;
import com.vien.smart_recipe_finder.dto.PostImageDTO;
import com.vien.smart_recipe_finder.dto.UpdatePostRequest;
import com.vien.smart_recipe_finder.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostDTO> createPost(@ModelAttribute CreatePostRequest request) {
        try {
            PostDTO post = postService.createPost(
                    request.getContent(),
                    request.getImages(),
                    request.getTags()
            );
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            System.err.println("Error creating post: " + e.getMessage());
            throw e;
        }
    }


    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long postId,
            @ModelAttribute UpdatePostRequest request
    )
    {
        System.out.println("Updating post ID: " + postId);
        try {
            PostDTO post = postService.updatePost(
                    postId,
                    request.getContent(),
                    request.getImages(),
                    request.getTags()
            );
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            System.err.println("Error updating post: " + e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        System.out.println("Deleting post ID: " + postId);
        try {
            postService.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error deleting post: " + e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getTimeline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getTimeline(page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostDTO>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getUserPosts(userId, page, size));
    }

    @GetMapping("/shared")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<PostDTO>> getSharedPosts(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Fetching shared posts for user ID: {}", userId);
        try {
            Page<PostDTO> sharedPosts = postService.getSharedPosts(userId, page, size);
            return ResponseEntity.ok(sharedPosts);
        } catch (Exception e) {
            logger.error("Error fetching shared posts: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/tag")
    public ResponseEntity<Page<PostDTO>> getPostsByTag(
            @RequestParam String tagName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getPostsByTag(tagName, page, size));
    }

    @GetMapping("/{postId}/images")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PostImageDTO>> getPostImages(@PathVariable Long postId) {
        logger.info("Fetching images for post ID: {}", postId);
        try {
            List<PostImageDTO> images = postService.getPostImages(postId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            logger.error("Error fetching images: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePostImage(@PathVariable Long imageId) {
        logger.info("Deleting image ID: {}", imageId);
        try {
            postService.deletePostImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting image ID: {} - {}", imageId, e.getMessage());
            throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
        }
    }
}