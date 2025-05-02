package com.vien.smart_recipe_finder.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentPostDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private Long postId;
    private Long parentId;
    private String content;
    private LocalDateTime createdAt;
}