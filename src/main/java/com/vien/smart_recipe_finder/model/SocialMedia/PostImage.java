package com.vien.smart_recipe_finder.model.SocialMedia;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "Post_Images")
@Data
@ToString(exclude = "post")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "image_data", nullable = false)
    private byte[] imageData;
}

