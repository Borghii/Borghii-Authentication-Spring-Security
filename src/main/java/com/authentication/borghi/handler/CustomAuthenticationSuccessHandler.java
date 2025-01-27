package com.authentication.borghi.handler;


import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.handler.auth.AuthenticationHandler;
import com.authentication.borghi.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Log
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private List<AuthenticationHandler> handlers = new ArrayList<>();

    public CustomAuthenticationSuccessHandler() {
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();

        handlers.stream()
                .filter(handler -> handler.supports(principal))
                .forEach(handler -> handler.handle(principal));

        response.sendRedirect("/home");

    }
}

//    private void handleOAuth2User(OAuth2User oAuth2User) {
//        try {
//            userService.saveOauthUser(oAuth2User);
//        } catch (UserAlreadyExist e) {
//            log.info("Usuario ya existe: "+ e.getMessage());
//        }
//    }
//
//    private void updateLastLogin(Object principal) {
//        String email = null;
//
//        if (principal instanceof OAuth2User oAuth2User) {
//            if (oAuth2User instanceof OidcUser oidcUser) {
//                email = oidcUser.getEmail();
//            } else {
//                email = oAuth2User.getAttribute("id") + "@gmail.com";
//            }
//        } else if (principal instanceof UserDetails userDetails) {
//            User user = userService.findUserByUsername(userDetails.getUsername());
//            if (user != null) {
//                email = user.getEmail();
//            }
//        }
//
//        if (email != null) {
//            userService.updateLastLoginByEmail(email, LocalDateTime.now());
//        }
//    }
//}
