package com.vien.smart_recipe_finder.service;

import com.vien.smart_recipe_finder.model.User;
import com.vien.smart_recipe_finder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);

            String providerId = oAuth2User.getAttribute("sub");
            String fullName = oAuth2User.getAttribute("name");
            String email = oAuth2User.getAttribute("email");
            String avatar = oAuth2User.getAttribute("picture");
            Date createdAt = new Date();

            logger.info("Processing OAuth2 user: providerId={}, email={}", providerId, email);

            Optional<User> existingUser = userRepository.findByProviderId(providerId);
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                user.setEmail(email);
                user.setFullName(fullName);
                user.setAvatar_url(avatar);
                user.setRole(user.getRole() != null ? user.getRole() : "ROLE_USER");
            } else {
                existingUser = userRepository.findByEmail(email);
                if (existingUser.isPresent()) {
                    user = existingUser.get();
                    user.setProviderId(providerId);
                    user.setProvider("Google");
                    user.setFullName(fullName);
                    user.setAvatar_url(avatar);
                } else {
                    user = new User(null, email, fullName, avatar, "Google", providerId, createdAt, "ROLE_USER");
                }
            }

            userRepository.save(user);
            logger.info("Saved user: id={}, email={}, providerId={}", user.getId(), user.getEmail(), user.getProviderId());

            List<GrantedAuthority> authorities = new ArrayList<>(oAuth2User.getAuthorities());
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
            logger.info("Authorities: {}", authorities);

            return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
        } catch (OAuth2AuthenticationException e) {
            logger.error("Error processing OAuth2 user: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return user.getId();
    }
}