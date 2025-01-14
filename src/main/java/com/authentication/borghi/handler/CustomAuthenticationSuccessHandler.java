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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

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

        if (principal instanceof OidcUser oidcUser){
            userService.updateLastLoginByEmail(oidcUser.getEmail(), LocalDateTime.now());
        }

        if (principal instanceof UserDetails userDetails){
            User user = userService.findUserByUsername(userDetails.getUsername());
            userService.updateLastLoginByEmail(user.getEmail(),LocalDateTime.now());
        }

        response.sendRedirect("/home");


    }
}
