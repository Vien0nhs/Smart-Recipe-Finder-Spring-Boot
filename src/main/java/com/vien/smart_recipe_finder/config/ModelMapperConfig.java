package com.vien.smart_recipe_finder.config;

import com.vien.smart_recipe_finder.dto.*;
import com.vien.smart_recipe_finder.model.History;
import com.vien.smart_recipe_finder.model.RecipeIngredients;
import com.vien.smart_recipe_finder.model.RecipeSpices;
import com.vien.smart_recipe_finder.model.Recipes;
import com.vien.smart_recipe_finder.model.SocialMedia.Post;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.util.Objects;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Recipes.class, RecipeDTO.class)
                .addMappings(mapper ->
                        mapper.map(Recipes::getImage, (dest, value) -> dest.setImage((String) value))
                );

        modelMapper.typeMap(RecipeIngredients.class, RecipeIngredientDTO.class)
                .addMappings(mapper -> {
                    mapper.when(Objects::nonNull).map(src -> src.getIngredient().getId(), RecipeIngredientDTO::setIngredientId);
                    mapper.when(Objects::nonNull).map(src -> src.getIngredient().getName(), RecipeIngredientDTO::setName);
                    mapper.when(Objects::nonNull).map(src -> src.getIngredient().getType()
                            != null ? src.getIngredient().getType().name() : null, RecipeIngredientDTO::setType);
                    mapper.map(RecipeIngredients::getIsMain, RecipeIngredientDTO::setIsMain);
                    mapper.map(RecipeIngredients::getQuantity, RecipeIngredientDTO::setQuantity);
                });

        modelMapper.typeMap(RecipeSpices.class, RecipeSpiceDTO.class)
                .addMappings(mapper -> {
                    mapper.when(Objects::nonNull).map(src -> src.getSpice().getId(), RecipeSpiceDTO::setSpiceId);
                    mapper.when(Objects::nonNull).map(src -> src.getSpice().getName(), RecipeSpiceDTO::setName);
                    mapper.map(RecipeSpices::getQuantity, RecipeSpiceDTO::setQuantity);
                });

        modelMapper.typeMap(History.class, HistoryDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getUser().getId(), HistoryDTO::setUserId);
                    mapper.map(src -> src.getRecipe().getId(), HistoryDTO::setRecipeId);
                    mapper.map(src -> src.getRecipe().getTitle(), HistoryDTO::setRecipeTitle);
                    mapper.map(src -> src.getRecipe().getImage(), HistoryDTO::setRecipeImage);
                });
        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setSkipNullEnabled(true);

        modelMapper.createTypeMap(Post.class, PostDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(PostDTO::setLikeCount);
                    mapper.skip(PostDTO::setCommentCount);
                    mapper.skip(PostDTO::setShareCount);
                    mapper.skip(PostDTO::setLikedByCurrentUser);
                });
        return modelMapper;
    }
}