package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.dto.CommentDTO;
import com.vien.smart_recipe_finder.dto.RatingDTO;
import com.vien.smart_recipe_finder.model.Rating;
import com.vien.smart_recipe_finder.model.Recipes;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.RatingRepository;
import com.vien.smart_recipe_finder.repository.RecipeRepository;
import com.vien.smart_recipe_finder.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public RatingDTO createOrUpdateRating(Long userId, Long recipeId, RatingDTO ratingDTO) {
        if (ratingDTO.getRating() < 1 || ratingDTO.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Recipes recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        Rating rating = ratingRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElse(new Rating());

        rating.setUser(user);
        rating.setRecipe(recipe);
        rating.setRating(ratingDTO.getRating());
        if (ratingDTO.getComment() != null && !ratingDTO.getComment().isEmpty()) {
            rating.setComment(ratingDTO.getComment());
        }
        if (rating.getId() == null) {
            rating.setCreatedAt(new Date());
        }

        ratingRepository.save(rating);
        return modelMapper.map(rating, RatingDTO.class);
    }

    public RatingDTO getUserRating(Long userId, Long recipeId) {
        Rating rating = ratingRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElse(null);
        return rating != null ? modelMapper.map(rating, RatingDTO.class) : null;
    }

    public Double getAverageRating(Long recipeId) {
        Double avg = ratingRepository.findAverageRatingByRecipeId(recipeId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public List<CommentDTO> getComments(Long recipeId) {
        List<Rating> ratings = ratingRepository.findByRecipeId(recipeId);
        return ratings.stream()
                .filter(r -> r.getComment() != null && !r.getComment().isEmpty())
                .map(r -> new CommentDTO(r.getUser().getFullName(), r.getComment(), r.getUser().getEmail()))
                .collect(Collectors.toList());
    }
    public void deleteComment(Long userId, Long recipeId) {
        Rating rating = ratingRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));

        User commentOwner = rating.getUser();

        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getRole());

        if (isAdmin || Objects.equals(commentOwner.getId(), currentUser.getId())) {
            ratingRepository.delete(rating);
        } else {
            throw new RuntimeException("Unauthorized to delete this comment");
        }
    }


    public RatingDTO updateComment(Long userId, Long recipeId, String comment) {
        Rating rating = ratingRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found"));
        rating.setComment(comment != null ? comment : "");
        ratingRepository.save(rating);
        return modelMapper.map(rating, RatingDTO.class);
    }
}