package com.vien.smart_recipe_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDTO {
    private String fullName;
    private String comment;
    private String email;
    private Long ratingId; // Thêm trường này
}