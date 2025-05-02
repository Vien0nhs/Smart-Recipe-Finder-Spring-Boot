package com.vien.smart_recipe_finder.dto;


import lombok.Data;

@Data
public class RecipeIngredientDTO {
    private Long ingredientId; // Lấy từ Ingredients.id
    private String name;       // Lấy từ Ingredients.name
    private String type;       // Lấy từ Ingredients.type (chuỗi từ enum IngredientType)
    private Boolean isMain;    // Từ RecipeIngredients.isMain
    private String quantity;   // Từ RecipeIngredients.quantity
}
