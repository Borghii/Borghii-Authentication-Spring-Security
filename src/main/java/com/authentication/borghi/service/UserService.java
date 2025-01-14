package com.authentication.borghi.service;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserService extends UserDetailsService {
    void saveUserFromDTO(UserDTO userDTO);
    void updateLastLoginByEmail(String email, LocalDateTime currentTime);
    User findUserByUsername(String username);
}
