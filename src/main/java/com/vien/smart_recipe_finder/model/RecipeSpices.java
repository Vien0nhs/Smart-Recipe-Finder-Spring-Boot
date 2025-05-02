package com.vien.smart_recipe_finder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Recipe_Spices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSpices {
    @EmbeddedId
    private RecipeSpicesId id;

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false) // Khóa ngoại tới Recipes
    private Recipes recipe;

    @ManyToOne
    @MapsId("spiceId")
    @JoinColumn(name = "spice_id", nullable = false) // Khóa ngoại tới Spices
    private Spices spice;

    @Column(name = "quantity")
    private String quantity;
}