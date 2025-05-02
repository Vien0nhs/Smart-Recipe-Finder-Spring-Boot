package com.vien.smart_recipe_finder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Recipe_Ingredients")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredients {
    @EmbeddedId
    private RecipeIngredientsId id;

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false) // Khóa ngoại tới Recipes
    private Recipes recipe;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id", nullable = false) // Khóa ngoại tới Ingredients
    private Ingredients ingredient;

    @Column(name = "is_main", nullable = false)
    private Boolean isMain;

    @Column(name = "quantity")
    private String quantity;
}