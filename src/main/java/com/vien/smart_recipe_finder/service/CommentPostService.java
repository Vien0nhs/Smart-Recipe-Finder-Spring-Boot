package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.dto.CommentPostDTO;
import com.vien.smart_recipe_finder.model.SocialMedia.CommentPost;
import com.vien.smart_recipe_finder.model.SocialMedia.NotificationType;
import com.vien.smart_recipe_finder.model.SocialMedia.Post;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.CommentPostRepository;
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
public class CommentPostService {
    @Autowired private CommentPostRepository commentRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private ModelMapper modelMapper;

    public CommentPostDTO createComment(Long postId, String content) {
        if (content == null || content.length() > 1000) throw new IllegalArgumentException("Invalid content");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        CommentPost comment = new CommentPost();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);

        commentRepository.save(comment);

        // Tạo thông báo cho chủ bài đăng
        if (!user.getId().equals(post.getUser().getId())) {
            notificationService.createNotification(
                    postId,
                    NotificationType.NEW_COMMENT,
                    user.getFullName() + " commented on your post",
                    post.getUser()
            );
        }

        return toDTO(comment);
    }

    @Transactional
    public CommentPostDTO updateComment(Long commentId, String content) {
        if (content == null || content.length() > 1000) throw new IllegalArgumentException("Invalid content");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentPost comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this comment");
        }

        comment.setContent(content);
        commentRepository.save(comment);
        return toDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentPost comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Kiểm tra xem user là chủ comment hoặc admin
        boolean isAdmin = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email) && u.getRole().equals("ROLE_ADMIN"));
        if (!comment.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public Page<CommentPostDTO> getComments(Long postId, int page, int size) {
        return commentRepository.findByPostIdAndParentIsNull(postId, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toDTO);
    }

    private CommentPostDTO toDTO(CommentPost comment) {
        CommentPostDTO dto = modelMapper.map(comment, CommentPostDTO.class);
        dto.setUserId(comment.getUser().getId());
        dto.setFullName(comment.getUser().getFullName());
        return dto;
    }
}