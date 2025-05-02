package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.dto.NotificationDTO;
import com.vien.smart_recipe_finder.model.SocialMedia.Notification;
import com.vien.smart_recipe_finder.model.SocialMedia.NotificationType;
import com.vien.smart_recipe_finder.model.SocialMedia.Post;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.NotificationRepository;
import com.vien.smart_recipe_finder.repository.PostRepository;
import com.vien.smart_recipe_finder.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ModelMapper modelMapper;

    public NotificationDTO createNotification(Long postId, NotificationType type, String content, User targetUser) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postId != null ? postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found")) : null;

        Notification notification = new Notification();
        notification.setUser(targetUser != null ? targetUser : currentUser);
        notification.setType(type);
        notification.setContent(content);
        notification.setPost(post);
        notification.setRead(false);

        notificationRepository.save(notification);
        return toDTO(notification);
    }

    public Page<NotificationDTO> getNotifications(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository
                .findByUserId(user.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toDTO);
    }

    public Page<NotificationDTO> getUnreadNotifications(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository
                .findByUserIdAndIsReadFalse(user.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toDTO);
    }

    public Long countUnreadNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationRepository.markAllAsReadByUserId(user.getId());
    }

    @Transactional
    public void clearAllNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        notificationRepository.deleteByUserId(user.getId());
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = modelMapper.map(notification, NotificationDTO.class);
        dto.setType(notification.getType().name());
        return dto;
    }
}