package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.dto.RecipeDTO;
import com.vien.smart_recipe_finder.model.RecipeType;
import com.vien.smart_recipe_finder.model.Recipes;
import com.vien.smart_recipe_finder.repository.RecipeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<RecipeDTO> getAllRecipes(int index, int page) {
        Pageable pageable = PageRequest.of(index, page);
        Page<Recipes> recipesPage = recipeRepository.findAll(pageable);
        return recipesPage.map(recipe -> modelMapper.map(recipe, RecipeDTO.class));
    }
    public RecipeDTO getRecipeById(Long id){
        Recipes recipes = recipeRepository.findById(id)
                .orElse(null);
        return modelMapper.map(recipes, RecipeDTO.class);

    }
    public Page<RecipeDTO> findRecipesByIngredientsLikePaged(
            List<String> ingredientNames,
            int page,
            int size
    ) {
        if (ingredientNames == null || ingredientNames.isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, size);
        Set<Long> recipeIds = new HashSet<>();

        for (String ingredientName : ingredientNames) {
            if (ingredientName != null && !ingredientName.trim().isEmpty()) {
                Page<Recipes> recipesPage = recipeRepository.findByIngredientNameLike(ingredientName, Pageable.unpaged());
                recipesPage.getContent().forEach(recipe -> recipeIds.add(recipe.getId()));
            }
        }

        if (recipeIds.isEmpty()) {
            return Page.empty();
        }

        Page<Recipes> recipesPage = recipeRepository.findByIdIn(new ArrayList<>(recipeIds), pageable);
        return recipesPage.map(recipe -> modelMapper.map(recipe, RecipeDTO.class));
    }

    public Page<RecipeDTO> findRecipesByTitleLike(String title, int page, int size) {
        if (title == null || title.trim().isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipes> recipesPage = recipeRepository.findByTitleExact(title.trim(), pageable);
        return recipesPage.map(recipe -> modelMapper.map(recipe, RecipeDTO.class));
    }

    public Page<RecipeDTO> findRecipesByCookingTime(int cookingTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipes> recipesPage = recipeRepository.findByCookingTimeLessThanOrEqual(cookingTime, pageable);
        return recipesPage.map(recipe -> modelMapper.map(recipe, RecipeDTO.class));
    }
    public Page<RecipeDTO> findRecipesByType(RecipeType recipeType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipes> recipesPage = recipeRepository.findByRecipeType(recipeType, pageable);
        return recipesPage.map(recipe -> modelMapper.map(recipe, RecipeDTO.class));
    }
}