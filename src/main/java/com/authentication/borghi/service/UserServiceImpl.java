package com.authentication.borghi.service;


import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.Role;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserDetail;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUserFromDTO(UserDTO userDTO){

        if (userRepository.existsByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail())){
            throw new UserAlreadyExist("Username or email already used");
        }

        User user = fromDTO(userDTO);

        user.setRole(new Role(user,"ROLE_USER"));

        if (! (user.getPassword() == null)) user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    private User fromDTO(UserDTO userDTO){
        User user = new User();

        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        //OAuth
        user.setProvider(userDTO.getProvider());
        user.setProviderId(userDTO.getProviderId());

        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        userDetail.setName(userDTO.getName());
        userDetail.setSurname(userDTO.getSurname());

        user.setUserDetail(userDetail);

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleName()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional
    public void updateLastLoginByEmail(String email, LocalDateTime currentTime) {
        userRepository.updateLastLoginByEmail(email,currentTime);
    }
}
