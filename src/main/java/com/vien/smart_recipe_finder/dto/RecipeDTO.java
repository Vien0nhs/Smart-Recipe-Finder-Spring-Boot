package com.vien.smart_recipe_finder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vien.smart_recipe_finder.model.RecipeType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RecipeDTO {
    private Long id;
    private String title;
    private String instructions;
    private String image;
    private RecipeType recipeType;
    private int cookingTime;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;
    private List<RecipeIngredientDTO> ingredients;
    private List<RecipeSpiceDTO> spices;
}


