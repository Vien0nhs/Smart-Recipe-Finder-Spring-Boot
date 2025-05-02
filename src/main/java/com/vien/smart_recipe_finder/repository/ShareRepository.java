package com.vien.smart_recipe_finder.repository;

import com.vien.smart_recipe_finder.model.SocialMedia.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShareRepository extends JpaRepository<Share, Long> {
    @Query("SELECT COUNT(s) FROM Share s WHERE s.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    List<Share> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Share s WHERE s.user.id = :userId AND s.post.id = :postId")
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}