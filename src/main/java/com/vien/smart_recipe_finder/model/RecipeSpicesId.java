package com.vien.smart_recipe_finder.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class RecipeSpicesId {
    private Long recipeId;
    private Long spiceId;
}
