package com.vien.smart_recipe_finder.repository;

import com.vien.smart_recipe_finder.model.SocialMedia.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PostImage pi WHERE pi.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostImage pi WHERE pi.id = :imageId")
    void deleteByImageId(@Param("imageId") Long imageId);


    @Query(value = "SELECT CONCAT('data:image/jpeg;base64,', TO_BASE64(image_data)) " +
            "FROM Post_Images WHERE post_id = :postId",
            nativeQuery = true)
    List<String> findUrlsByPostId(@Param("postId") Long postId);

}

