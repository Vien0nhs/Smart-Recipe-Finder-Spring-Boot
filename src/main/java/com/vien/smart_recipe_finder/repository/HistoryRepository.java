package com.vien.smart_recipe_finder.repository;

import com.vien.smart_recipe_finder.model.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    Page<History> findByUserId(Long userId, Pageable pageable);
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);

    Optional<History> findByUserIdAndRecipeId(Long userId, Long recipeId);
}