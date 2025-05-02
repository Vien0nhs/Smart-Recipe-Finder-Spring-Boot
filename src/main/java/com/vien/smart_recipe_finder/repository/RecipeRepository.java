package com.vien.smart_recipe_finder.repository;

import com.vien.smart_recipe_finder.model.RecipeType;
import com.vien.smart_recipe_finder.model.Recipes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipes, Long> {
    Page<Recipes> findAll(Pageable pageable);
    Page<Recipes> findByIdIn(List<Long> ids, Pageable pageable);

    @Query("SELECT DISTINCT r FROM Recipes r " +
            "JOIN r.ingredients ri " +
            "JOIN ri.ingredient i " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredientName, '%'))")
    Page<Recipes> findByIngredientNameLike(
            @Param("ingredientName") String ingredientName,
            Pageable pageable
    );
    @Query("SELECT r FROM Recipes r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Recipes> findByTitleExact(
            @Param("title") String title,
            Pageable pageable
    );
    @Query("SELECT r FROM Recipes r " +
            "WHERE r.cookingTime = :cookingTime")
    Page<Recipes> findByCookingTimeLessThanOrEqual(
            @Param("cookingTime") int cookingTime,
            Pageable pageable
    );
    @Query("SELECT r FROM Recipes r WHERE (:recipeType IS NULL OR r.recipeType = :recipeType)")
    Page<Recipes> findByRecipeType(
            @Param("recipeType") RecipeType recipeType,
            Pageable pageable
    );
}
