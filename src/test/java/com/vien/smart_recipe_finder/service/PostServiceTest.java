package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.dto.PostDTO;
import com.vien.smart_recipe_finder.model.SocialMedia.Post;
import com.vien.smart_recipe_finder.model.SocialMedia.Tag;
import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostImageRepository postImageRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentPostRepository commentPostRepository;
    @Mock
    private ShareRepository shareRepository;
    @Mock
    private ShareService shareService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreatePostWithContentAndTags() {
        String content = "This is a test post";
        List<MultipartFile> images = null;
        List<String> tags = Arrays.asList("Spring", "Recipe");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Tag springTag = new Tag();
        springTag.setName("spring");
        Tag recipeTag = new Tag();
        recipeTag.setName("recipe");

        when(tagRepository.findByName("spring")).thenReturn(null);
        when(tagRepository.findByName("recipe")).thenReturn(null);

        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(100L); // giả lập ID sau khi lưu
            return post;
        });

        PostDTO dto = postService.createPost(content, images, tags);

        assertNotNull(dto);
        assertEquals("This is a test post", dto.getContent());
        assertEquals(100L, dto.getId());
        verify(postRepository, times(1)).save(any(Post.class));
    }
}
