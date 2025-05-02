package com.vien.smart_recipe_finder.dto;

import lombok.Data;

@Data
public class RecipeSpiceDTO {
    private Long spiceId;    // Lấy từ Spices.id
    private String name;     // Lấy từ Spices.name
    private String quantity; // Từ RecipeSpices.quantity
}