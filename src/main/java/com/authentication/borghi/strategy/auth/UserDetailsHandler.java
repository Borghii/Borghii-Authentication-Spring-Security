package com.authentication.borghi.strategy.auth;

import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class UserDetailsHandler implements AuthenticationHandler {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Object principal) {
        return principal instanceof UserDetails;
    }

    @Override
    public void handle(Object principal) {
        UserDetails userDetails = (UserDetails) principal;
        User user = userService.findUserByUsername(userDetails.getUsername());
        if (user != null) {
            userService.updateLastLoginByEmail(user.getEmail(), LocalDateTime.now());
        }
    }

    @Override
    public Map<String, Object> getAttributes(Object principal) {
        UserDetails userDetails = (UserDetails) principal;
        return Map.of("name", userDetails.getUsername());
    }
}

