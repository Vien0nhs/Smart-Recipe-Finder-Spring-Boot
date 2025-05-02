package com.vien.smart_recipe_finder.repository;

import com.vien.smart_recipe_finder.model.SocialMedia.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.user.id = :userId")
    long countByPostUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);
}
