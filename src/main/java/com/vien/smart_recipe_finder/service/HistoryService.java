package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.dto.HistoryDTO;
import com.vien.smart_recipe_finder.model.History;
import com.vien.smart_recipe_finder.model.Recipes;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.HistoryRepository;
import com.vien.smart_recipe_finder.repository.RecipeRepository;
import com.vien.smart_recipe_finder.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class HistoryService {
    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public HistoryDTO saveHistory(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Recipes recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        History history = historyRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElse(new History());

        history.setUser(user);
        history.setRecipe(recipe);
        if (history.getId() == null) {
            history.setCookedAt(new Date());
        }

        historyRepository.save(history);
        return modelMapper.map(history, HistoryDTO.class);
    }

    public Page<HistoryDTO> getUserHistory(Long userId, Pageable pageable) {
        Page<History> historyPage = historyRepository.findByUserId(userId, pageable);
        return historyPage.map(history -> modelMapper.map(history, HistoryDTO.class));
    }

    @Transactional
    public void deleteHistory(Long userId, Long recipeId) {
        historyRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}