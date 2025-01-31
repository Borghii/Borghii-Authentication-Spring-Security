package com.authentication.borghi.service.user;


import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.Role;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserMapper;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;


import java.time.LocalDateTime;
import java.util.Collections;

@Log
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    @Override
    @Transactional
    public void saveUserFromDTO(UserDTO userDTO){

        if (userRepository.existsByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail())){
            throw new UserAlreadyExist("Username or email already used");
        }

        User user = userMapper.fromDTO(userDTO);

        user.setRole(new Role(user,"ROLE_USER"));

        if (! (user.getPassword() == null)) user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("User saved: "+userDTO.getEmail());

        userRepository.save(user);
    }

    @Override
    public void saveOauthUser(OAuth2User oAuth2User) {
        saveUserFromDTO(userMapper.fromOAuth2User(oAuth2User));
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
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateLastLoginByEmail(String email, LocalDateTime currentTime) {
        userRepository.updateLastLoginByEmail(email,currentTime);
    }
}
