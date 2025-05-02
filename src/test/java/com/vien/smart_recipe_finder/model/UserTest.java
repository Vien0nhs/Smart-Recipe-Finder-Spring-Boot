package com.vien.smart_recipe_finder.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();
        Date now = new Date();

        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Nguyễn Văn A");
        user.setAvatar_url("avatar.png");
        user.setProvider("google");
        user.setProviderId("1234567890");
        user.setCreatedAt(now);
        user.setRole("ROLE_USER");

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Nguyễn Văn A", user.getFullName());
        assertEquals("avatar.png", user.getAvatar_url());
        assertEquals("google", user.getProvider());
        assertEquals("1234567890", user.getProviderId());
        assertEquals(now, user.getCreatedAt());
        assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        Date now = new Date();
        User user1 = new User(1L, "a@example.com", "Nguyễn A", "ava.png", "google", "123", now, "ROLE_USER");
        User user2 = new User(1L, "a@example.com", "Nguyễn A", "ava.png", "google", "123", now, "ROLE_USER");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        Date now = new Date();
        User user = new User(1L, "a@example.com", "Nguyễn A", "ava.png", "google", "123", now, "ROLE_USER");

        String toString = user.toString();
        assertTrue(toString.contains("a@example.com"));
        assertTrue(toString.contains("Nguyễn A"));
    }
}
