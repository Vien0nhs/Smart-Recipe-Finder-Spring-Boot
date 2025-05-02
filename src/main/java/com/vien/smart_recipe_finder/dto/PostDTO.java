package com.vien.smart_recipe_finder.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String avatar_url;
    private String username;
    private String content;
    private List<String> imageUrls;
    private List<String> tags;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private boolean likedByCurrentUser;
    private LocalDateTime createdAt;
}

