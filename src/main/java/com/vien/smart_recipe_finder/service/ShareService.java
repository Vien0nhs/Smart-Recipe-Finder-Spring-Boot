package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.model.SocialMedia.Post;
import com.vien.smart_recipe_finder.model.SocialMedia.Share;
import com.vien.smart_recipe_finder.model.SocialMedia.NotificationType;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.PostRepository;
import com.vien.smart_recipe_finder.repository.ShareRepository;
import com.vien.smart_recipe_finder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {
    private static final Logger logger = LoggerFactory.getLogger(ShareService.class);
    @Autowired private ShareRepository shareRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;

    @Transactional
    public void sharePost(Long postId) {
        logger.info("Sharing post ID: {}", postId);
        try {
            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
            if (!isAuthenticated) {
                logger.warn("Anonymous user attempted to share post ID: {}", postId);
                throw new IllegalStateException("User must be authenticated to share a post");
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("Current user email during share: {}", email);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

            if (shareRepository.existsByUserIdAndPostId(user.getId(), postId)) {
                logger.warn("User {} already shared post {}", user.getId(), postId);
                throw new IllegalStateException("You have already shared this post");
            }

            Share share = new Share();
            share.setUser(user);
            share.setPost(post);
            shareRepository.save(share);
            logger.info("Successfully shared post ID: {} by user ID: {}", postId, user.getId());

            if (!user.getId().equals(post.getUser().getId())) {
                notificationService.createNotification(
                        postId,
                        NotificationType.POST_SHARED,
                        user.getFullName() + " shared your post",
                        post.getUser()
                );
            }
        } catch (Exception e) {
            logger.error("Error sharing post ID: {} - {}", postId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void unSharePost(Long postId) {
        logger.info("Unsharing post ID: {}", postId);
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));

            if (!shareRepository.existsByUserIdAndPostId(user.getId(), postId)) {
                logger.warn("User {} has not shared post {}", user.getId(), postId);
                throw new IllegalStateException("You have not shared this post");
            }

            shareRepository.deleteByUserIdAndPostId(user.getId(), postId);
            logger.info("Successfully unshared post ID: {} by user ID: {}", postId, user.getId());
        } catch (Exception e) {
            logger.error("Error unsharing post ID: {} - {}", postId, e.getMessage());
            throw e;
        }
    }

    public List<Long> getSharedPostIdsByUserId(Long userId) {
        return shareRepository.findByUserId(userId).stream()
                .map(share -> share.getPost().getId())
                .collect(Collectors.toList());
    }
}