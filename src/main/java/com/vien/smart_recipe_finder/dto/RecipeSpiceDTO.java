package com.vien.smart_recipe_finder.dto;

import lombok.Data;

@Data
public class RecipeSpiceDTO {
    private Long spiceId;
    private String name;
    private String quantity;
}