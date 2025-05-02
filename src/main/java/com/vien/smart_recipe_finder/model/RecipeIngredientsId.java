package com.vien.smart_recipe_finder.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
@Embeddable
public class RecipeIngredientsId implements Serializable {
    private Long recipeId;
    private Long ingredientId;
}
