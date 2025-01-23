package com.authentication.borghi.service;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserService extends UserDetailsService {
    void saveUserFromDTO(UserDTO userDTO);
    void saveOauthUser(OAuth2User oAuth2User);
    void processAuthenticatedUser(Object principal, Model model);
    void updateLastLoginByEmail(String email, LocalDateTime currentTime);
    User findUserByUsername(String username);
}
