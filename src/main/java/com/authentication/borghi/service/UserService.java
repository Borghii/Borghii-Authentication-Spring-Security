package com.authentication.borghi.service;

import com.authentication.borghi.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void save(User user);
}
