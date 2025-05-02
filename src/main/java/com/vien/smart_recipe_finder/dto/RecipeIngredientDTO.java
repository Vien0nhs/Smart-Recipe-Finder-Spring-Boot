package com.vien.smart_recipe_finder.dto;


import lombok.Data;

@Data
public class RecipeIngredientDTO {
    private Long ingredientId;
    private String name;
    private String type;
    private Boolean isMain;
    private String quantity;
}
