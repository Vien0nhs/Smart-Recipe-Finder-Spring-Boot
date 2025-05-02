package com.vien.smart_recipe_finder.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private long postCount;
    private long likeCount;
}