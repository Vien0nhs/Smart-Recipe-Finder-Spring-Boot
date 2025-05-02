package com.vien.smart_recipe_finder.repository;

import com.vien.smart_recipe_finder.model.SocialMedia.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);
}