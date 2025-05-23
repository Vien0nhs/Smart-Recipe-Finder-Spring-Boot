package com.vien.smart_recipe_finder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Welcome to Smart Recipe Finder";
    }
    @GetMapping("/Success")
    public String success() {
        return "Login Successful";
    }
}
