package com.vien.smart_recipe_finder.controller;

import com.vien.smart_recipe_finder.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gemini")
@Tag(name = "Gemini API", description = "API để tương tác với Google Gemini")
public class GeminiController {
    @Autowired
    private GeminiService geminiService;

    @PostMapping("/chat")
    @Operation(summary = "Gửi prompt đến Gemini API", description = "Nhận nội dung sinh ra từ prompt")
    public String chat(@RequestBody String prompt) throws Exception {
        return geminiService.generateContent(prompt);
    }
}
