package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.dto.RecipeDTO;
import com.vien.smart_recipe_finder.model.RecipeType;
import com.vien.smart_recipe_finder.model.Recipes;
import com.vien.smart_recipe_finder.repository.RecipeRepository;
import com.vien.smart_recipe_finder.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/recipes")
public class RecipeSuggestController {
    @Autowired
    private RecipeService recipeService;
    @GetMapping
    public Page<RecipeDTO> getAllRecipe(
            @RequestParam(defaultValue = "1") int page,    // Số trang, mặc định là 0
            @RequestParam(defaultValue = "12") int size
    ){
        return recipeService.getAllRecipes(page - 1 , size);
    }
    // Nếu muốn phân trang
    @GetMapping("/search-by-ingredients")
    public Page<RecipeDTO> searchRecipesByIngredientsPaged(
            @RequestParam(required = false) String ingredient1,
            @RequestParam(required = false) String ingredient2,
            @RequestParam(required = false) String ingredient3,
            @RequestParam(required = false) String ingredient4,
            @RequestParam(required = false) String ingredient5,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {
        List<String> ingredientNames = Stream.of(ingredient1, ingredient2, ingredient3, ingredient4, ingredient5)
                // Tạo các tham số thành một "luồng dữ liệu"
                .filter(name -> name != null && !name.trim().isEmpty())
                // Lọc các tham số bỏ các phần tử bị null hoặc chuỗi trống
                .collect(Collectors.toList());
                // Chuyển kết quả về List<String>

        return recipeService.findRecipesByIngredientsLikePaged(ingredientNames, page-1, size);
    }
    // Tìm theo title
    @GetMapping("/search-by-title")
    public Page<RecipeDTO> searchRecipesByTitle(
            @RequestParam("title") String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int size) {
        return recipeService.findRecipesByTitleLike(title, page-1, size);
    }
    // Tìm theo cooking_time
    @GetMapping("/search-by-cooking-time")
    public Page<RecipeDTO> searchRecipesByCookingTime(
            @RequestParam("cookingTime") int cookingTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return recipeService.findRecipesByCookingTime(cookingTime, page-1, size);
    }
    // Lọc theo loại công thức
    // Endpoint lọc theo loại công thức với suggest
    @GetMapping("/search-by-type")
    public Object searchByType(
            @RequestParam(value = "recipeType", required = false) String recipeType,
            @RequestParam(defaultValue = "false") boolean suggest,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (suggest) {
            // Trả về danh sách gợi ý các loại công thức
            return Arrays.stream(RecipeType.values())
                    .map(type -> type.name() + " (" + type.getDisplayName() + ")")
                    .collect(Collectors.toList());
        }

        // Lọc công thức nếu không yêu cầu suggest
        RecipeType type = null;
        if (recipeType != null) {
            try {
                type = RecipeType.valueOf(recipeType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid recipe type: " + recipeType);
            }
        }
        return recipeService.findRecipesByType(type, page-1, size);
    }
    @GetMapping("/{id}")
    public RecipeDTO getRecipeById(@PathVariable Long id) {
        System.out.println(recipeService.getRecipeById(id));
        return recipeService.getRecipeById(id);
    }
}
