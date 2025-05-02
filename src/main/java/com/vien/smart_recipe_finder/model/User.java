package com.vien.smart_recipe_finder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "full_name")
    private String fullName;  // Đổi từ name → fullName để khớp DB
    private String avatar_url;
    @Column(nullable = false)
    private String provider; // google, facebook, github
    @Column(name = "provider_id", nullable = false, unique = true)
    private String providerId;  // Đổi từ googleId → providerId
    @Column(name = "created_at", updatable = false) // Không cho update created_at
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(nullable = false)
    private String role;

}
