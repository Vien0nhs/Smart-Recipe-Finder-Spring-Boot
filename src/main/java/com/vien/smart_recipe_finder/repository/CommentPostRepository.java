package com.vien.smart_recipe_finder.repository;

import com.vien.smart_recipe_finder.model.SocialMedia.CommentPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentPostRepository extends JpaRepository<CommentPost, Long> {
    Page<CommentPost> findByPostId(Long postId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CommentPost c WHERE c.post.id = :postId")
    int countByPostId(@Param("postId") Long postId);

    Page<CommentPost> findByPostIdAndParentIsNull(Long postId, Pageable pageable);

}