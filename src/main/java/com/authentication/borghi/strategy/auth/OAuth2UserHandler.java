package com.authentication.borghi.strategy.auth;

import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Log
@Component
public class OAuth2UserHandler implements AuthenticationHandler {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Object principal) {
        return principal instanceof OAuth2User;
    }

    @Override
    public void handle(Object principal) {
        OAuth2User oAuth2User = (OAuth2User) principal;

        saveOauthUser(oAuth2User);

        updateLastLoginByEmail(oAuth2User);
    }

    private void saveOauthUser(OAuth2User oAuth2User){
        try {
            userService.saveOauthUser(oAuth2User);
        } catch (UserAlreadyExist e) {
            log.info(e.getMessage());
        }
    }

    private void updateLastLoginByEmail(OAuth2User oAuth2User) {
        String email = null;

        if (oAuth2User instanceof OidcUser oidcUser) {
            email = oidcUser.getEmail();
        }else if (oAuth2User instanceof DefaultOAuth2User defaultOAuth2User){
            Map<String, Object> attributes = defaultOAuth2User.getAttributes();
            email = attributes.get("id") + "@gmail.com";
        }

        if (email != null) {
            userService.updateLastLoginByEmail(email, LocalDateTime.now());
        }
    }

    @Override
    public Map<String, Object> getAttributes(Object principal) {
        OAuth2User oAuth2User = (OAuth2User) principal;
        return Map.of("name", Objects.requireNonNull(oAuth2User.getAttribute("name")));
    }
}

