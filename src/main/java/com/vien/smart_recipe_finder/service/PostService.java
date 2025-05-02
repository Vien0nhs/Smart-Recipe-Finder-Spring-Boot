package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.dto.PostDTO;
import com.vien.smart_recipe_finder.dto.PostImageDTO;
import com.vien.smart_recipe_finder.dto.UserProfileDTO;
import com.vien.smart_recipe_finder.model.SocialMedia.*;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.*;
import net.coobird.thumbnailator.Thumbnails;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired private PostRepository postRepository;
    @Autowired private PostImageRepository postImageRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private LikeRepository likeRepository;
    @Autowired private CommentPostRepository commentPostRepository;
    @Autowired private ShareRepository shareRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private ModelMapper modelMapper;
    @Autowired private ShareService shareService;

    public PostDTO createPost(String content, List<MultipartFile> images, List<String> tagNames) {
        System.out.println("Creating post with content: " + content);
        System.out.println("Images: " + (images != null ? images.size() : 0));
        System.out.println("Tags: " + (tagNames != null ? tagNames : "none"));
        if (content == null || content.length() > 10000) throw new IllegalArgumentException("Invalid content");
        if (images != null && images.size() > 3) throw new IllegalArgumentException("Max 3 images");
        if (tagNames != null && tagNames.size() > 5) throw new IllegalArgumentException("Max 5 tags");

        OAuth2User oauth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = oauth2User.getAttribute("email");
        System.out.println("User email: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);

        if (images != null) {
            List<PostImage> postImages = new ArrayList<>();
            for (MultipartFile image : images) {
                try {
                    if (image.getSize() > 16 * 1024 * 1024) throw new IllegalArgumentException("Image too large (>16MB)");
                    System.out.println("Processing image: " + image.getOriginalFilename());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Thumbnails.of(image.getInputStream())
                            .size(1280, 720)
                            .outputQuality(0.8)
                            .outputFormat("jpg")
                            .toOutputStream(out);
                    byte[] compressed = out.toByteArray();
                    if (compressed.length > 16 * 1024 * 1024) throw new IllegalArgumentException("Compressed image too large");
                    PostImage postImage = new PostImage();
                    postImage.setPost(post);
                    postImage.setImageData(compressed);
                    postImages.add(postImage);
                } catch (Exception e) {
                    System.err.println("Image processing failed: " + e.getMessage());
                    throw new RuntimeException("Image processing failed: " + e.getMessage());
                }
            }
            post.setImages(postImages);
        }

        if (tagNames != null) {
            List<Tag> tags = tagNames.stream()
                    .map(name -> {
                        System.out.println("Processing tag: " + name);
                        Tag tag = tagRepository.findByName(name.toLowerCase());
                        if (tag == null) {
                            tag = new Tag();
                            tag.setName(name.toLowerCase());
                            tagRepository.save(tag);
                        }
                        return tag;
                    })
                    .collect(Collectors.toList());
            post.setTags(tags);
        }

        System.out.println("Saving post to DB");
        postRepository.save(post);
        System.out.println("Post saved");
        return toDTO(post);
    }

    public Page<PostDTO> getUserPosts(Long userId, int page, int size) {
        return postRepository.findByUserId(userId, PageRequest.of(page, size))
                .map(this::toDTO);
    }

    public Page<PostDTO> getSharedPosts(Long userId, int page, int size) {
        List<Long> sharedPostIds = shareService.getSharedPostIdsByUserId(userId);
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByIdIn(sharedPostIds, pageable).map(this::toDTO);
    }

    public Page<PostDTO> getPostsByTag(String tagName, int page, int size) {
        return postRepository.findByTagsName(tagName.toLowerCase(), PageRequest.of(page, size))
                .map(this::toDTO);
    }

    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatar_url());
        dto.setPostCount(postRepository.countByUserId(userId));
        dto.setLikeCount(likeRepository.countByPostUserId(userId));
        return dto;
    }

    public Page<PostDTO> getTimeline(int page, int size) {
        System.out.println("Fetching timeline: page=" + page + ", size=" + size);
        try {
            Page<PostDTO> result = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                    .map(this::toDTO);
            System.out.println("Timeline fetched: " + result.getContent().size() + " posts");
            return result;
        } catch (Exception e) {
            System.err.println("Error fetching timeline: " + e.getMessage());
            throw e;
        }
    }

    private PostDTO toDTO(Post post) {
        logger.info("Mapping post ID: {}", post.getId());
        try {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setUserId(post.getUser().getId());
            dto.setUsername(post.getUser().getFullName());
            dto.setAvatar_url(post.getUser().getAvatar_url());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setImageUrls(postImageRepository.findUrlsByPostId(post.getId()));
            dto.setTags(post.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
            dto.setLikeCount(likeRepository.countByPostId(post.getId()));
            dto.setCommentCount(commentPostRepository.countByPostId(post.getId()));
            dto.setShareCount(shareRepository.countByPostId(post.getId()));

            boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
            if (isAuthenticated) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByEmail(email).orElse(null);
                dto.setLikedByCurrentUser(user != null && likeRepository.existsByUserIdAndPostId(user.getId(), post.getId()));
            } else {
                dto.setLikedByCurrentUser(false);
            }

            return dto;
        } catch (Exception e) {
            logger.error("Error mapping post ID: {}, error: {}", post.getId(), e.getMessage());
            throw new RuntimeException("Failed to map post to DTO", e);
        }
    }

    public PostDTO updatePost(Long postId, String content, List<MultipartFile> images, List<String> tagNames) {
        logger.info("Updating post ID: {}", postId);
        OAuth2User oauth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = oauth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        if (content == null || content.length() > 10000) throw new IllegalArgumentException("Invalid content");
        if (tagNames != null && tagNames.size() > 5) throw new IllegalArgumentException("Max 5 tags");

        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());

        List<PostImage> currentImages = new ArrayList<>(post.getImages());
        logger.info("Current images count: {}", currentImages.size());
        List<PostImage> newImages = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                try {
                    if (image.getSize() > 16 * 1024 * 1024) throw new IllegalArgumentException("Image too large (>16MB)");
                    logger.info("Processing new image: {}", image.getOriginalFilename());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Thumbnails.of(image.getInputStream())
                            .size(1280, 720)
                            .outputQuality(0.8)
                            .outputFormat("jpg")
                            .toOutputStream(out);
                    byte[] compressed = out.toByteArray();
                    if (compressed.length > 16 * 1024 * 1024) throw new IllegalArgumentException("Compressed image too large");
                    PostImage postImage = new PostImage();
                    postImage.setPost(post);
                    postImage.setImageData(compressed);
                    newImages.add(postImage);
                } catch (Exception e) {
                    logger.error("Image processing failed: {}", e.getMessage());
                    throw new RuntimeException("Image processing failed: " + e.getMessage());
                }
            }

            List<PostImage> combinedImages = new ArrayList<>();
            combinedImages.addAll(newImages);
            int remainingSlots = 3 - combinedImages.size();
            if (remainingSlots > 0) {
                combinedImages.addAll(currentImages.subList(0, Math.min(remainingSlots, currentImages.size())));
            }

            if (combinedImages.size() > 3) {
                throw new IllegalArgumentException("Total images cannot exceed 3");
            }

            post.getImages().clear();
            post.getImages().addAll(combinedImages);
        } else {
            logger.info("No new images provided, keeping existing images");
        }

        post.getTags().clear();
        if (tagNames != null) {
            List<Tag> tags = tagNames.stream()
                    .map(name -> {
                        Tag tag = tagRepository.findByName(name.toLowerCase());
                        if (tag == null) {
                            tag = new Tag();
                            tag.setName(name.toLowerCase());
                            tagRepository.save(tag);
                        }
                        return tag;
                    })
                    .collect(Collectors.toList());
            post.setTags(tags);
        }

        logger.info("Saving updated post ID: {}", postId);
        postRepository.save(post);
        return toDTO(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        OAuth2User oauth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = oauth2User.getAttribute("email");
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User postOwner = post.getUser();

        boolean isAdmin = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email) && Objects.equals(u.getRole(), "ROLE_ADMIN"));

        if (isAdmin && !postOwner.getId().equals(currentUser.getId())) {
            // Admin xóa bài, tạo thông báo cho chủ bài
            notificationService.createNotification(
                    postId,
                    NotificationType.POST_DELETED,
                    "Your post was deleted by an admin",
                    postOwner
            );
            postRepository.delete(post);
            return;
        }

        if (!postOwner.getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        postRepository.delete(post);
    }

    public List<PostImageDTO> getPostImages(Long postId) {
        logger.info("Fetching images for post ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return post.getImages().stream()
                .map(img -> {
                    PostImageDTO dto = new PostImageDTO();
                    dto.setId(img.getId());
                    dto.setUrl("data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(img.getImageData()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePostImage(Long imageId) {
        logger.info("Deleting image ID: {}", imageId);
        try {
            OAuth2User oauth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = oauth2User.getAttribute("email");
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));

            PostImage image = postImageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));
            Post post = image.getPost();
            if (post == null) {
                throw new RuntimeException("Post not found for image: " + imageId);
            }
            if (!post.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to delete image: " + imageId);
            }

            postImageRepository.deleteByImageId(imageId);
            logger.info("Successfully deleted image ID: {}", imageId);
        } catch (Exception e) {
            logger.error("Error deleting image ID: {} - {}", imageId, e.getMessage());
            throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
        }
    }
}