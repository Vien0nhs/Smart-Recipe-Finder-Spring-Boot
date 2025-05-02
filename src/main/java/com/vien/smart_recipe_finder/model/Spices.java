package com.vien.smart_recipe_finder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Spices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true) // Tên gia vị không null và duy nhất
    private String name;
}