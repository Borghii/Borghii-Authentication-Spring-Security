package com.authentication.borghi.handler;


import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private UserService userService;

    public CustomAuthenticationSuccessHandler() {
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            if (oAuth2User instanceof OidcUser oidcUser) {
                updateLastLogin(oidcUser.getEmail());
            } else {
                updateLastLogin(oAuth2User.getAttribute("id") + "@gmail.com");
            }
        } else if (principal instanceof UserDetails userDetails) {
            User user = userService.findUserByUsername(userDetails.getUsername());
            updateLastLogin(user.getEmail());
        }

        response.sendRedirect("/home");

    }

    private void updateLastLogin(String email) {
        if (email != null) {
            userService.updateLastLoginByEmail(email, LocalDateTime.now());
        }
    }
}
