package com.vien.smart_recipe_finder.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Recipes") // Tên bảng trong DB
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false) // Đảm bảo title không null
    private String title;

    @Column(name = "instructions", nullable = false, columnDefinition = "TEXT") // Instructions là TEXT trong DB
    private String instructions;

    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipe_type", columnDefinition = "ENUM('CANH', 'KHO', 'XAO', 'CHIEN_LUOC', 'NUONG_HAP')")
    private RecipeType recipeType;

    @Column(name = "created_at", updatable = false) // Không cho update created_at
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    @Column(name = "cooking_time", nullable = false)
    private int cookingTime = 0; // Thêm field cookingTime, mặc định là 0

    // Quan hệ với RecipeIngredients (1 công thức có nhiều nguyên liệu)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredients> ingredients;

    // Quan hệ với RecipeSpices (1 công thức có nhiều gia vị)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeSpices> spices;

}