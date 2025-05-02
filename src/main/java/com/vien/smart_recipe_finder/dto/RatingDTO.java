package com.vien.smart_recipe_finder.dto;

import lombok.Data;

@Data
public class RatingDTO {
    private Long id;
    private Long userId;
    private Long recipeId;
    private int rating;
    private String comment;
}