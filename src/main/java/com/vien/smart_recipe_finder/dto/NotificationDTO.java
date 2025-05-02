package com.vien.smart_recipe_finder.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String type;
    private String content;
    private Long postId;
    private boolean isRead;
    private LocalDateTime createdAt;
}