package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.model.SocialMedia.Like;
import com.vien.smart_recipe_finder.model.SocialMedia.NotificationType;
import com.vien.smart_recipe_finder.model.SocialMedia.Post;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.LikeRepository;
import com.vien.smart_recipe_finder.repository.PostRepository;
import com.vien.smart_recipe_finder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);
    @Autowired private LikeRepository likeRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;

    @Transactional
    public void likePost(Long postId) {
        logger.info("Liking post ID: {}", postId);
        try {
            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
            if (!isAuthenticated) {
                logger.warn("Anonymous user attempted to like post ID: {}", postId);
                throw new IllegalStateException("User must be authenticated to like a post");
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Current user email during like: {}", email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

            if (likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
                logger.info("User {} already liked post {}, unliking instead", user.getId(), postId);
                likeRepository.deleteByUserIdAndPostId(user.getId(), postId);
                return;
            }

            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            logger.info("Successfully liked post ID: {} by user ID: {}", postId, user.getId());

            if (!user.getId().equals(post.getUser().getId())) {
                notificationService.createNotification(
                        postId,
                        NotificationType.POST_LIKED,
                        user.getFullName() + " liked your post",
                        post.getUser()
                );
            }
        } catch (Exception e) {
            logger.error("Error liking post ID: {} - {}", postId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void unlikePost(Long postId) {
        logger.info("Unliking post ID: {}", postId);
        try {
            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
            if (!isAuthenticated) {
                logger.warn("Anonymous user attempted to unlike post ID: {}", postId);
                throw new IllegalStateException("User must be authenticated to unlike a post");
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Current user email during unlike: {}", email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            likeRepository.deleteByUserIdAndPostId(user.getId(), postId);
            logger.info("Successfully unliked post ID: {} by user ID: {}", postId, user.getId());
        } catch (Exception e) {
            logger.error("Error unliking post ID: {} - {}", postId, e.getMessage());
            throw e;
        }
    }
}