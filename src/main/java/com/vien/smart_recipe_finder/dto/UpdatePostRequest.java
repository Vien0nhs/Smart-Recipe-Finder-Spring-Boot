package com.vien.smart_recipe_finder.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class UpdatePostRequest {
    private String content;
    private List<MultipartFile> images;
    private List<String> tags;
}
