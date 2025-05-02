package com.vien.smart_recipe_finder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class HistoryDTO {
    private Long id;
    private Long userId;
    private Long recipeId;
    private String recipeTitle;
    private String recipeImage;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date cookedAt;
}