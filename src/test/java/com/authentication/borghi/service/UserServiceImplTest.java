package com.authentication.borghi.service;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.Role;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserDetail;
import com.authentication.borghi.entity.user.UserMapper;
import com.authentication.borghi.repository.UserRepository;
import com.authentication.borghi.service.user.UserService;
import com.authentication.borghi.service.user.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void shouldSaveUserFromDTO() {
        // Given
        UserDTO userDTO = createTestUserDTO();
        User user = createTestUser(userDTO);
        

        // When
        userService.saveUserFromDTO(userDTO);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(capturedUser.getUserDetail().getName()).isEqualTo(userDTO.getName());
        assertThat(capturedUser.getUserDetail().getSurname()).isEqualTo(userDTO.getSurname());
        assertThat(capturedUser.getEmail()).isEqualTo(userDTO.getEmail());


        assertThat(passwordEncoder.matches(userDTO.getPassword(), capturedUser.getPassword())).isTrue();
        assertThat(capturedUser.getRole().getRoleName()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        // Given
        String username = "not_exist";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with username: " + username);

        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldReturnUserWhenUsernameExists() {
        // Given
        String username = "testUser";
        User user = createTestUser(createTestUserDTO());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        User result = userService.findUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        verify(userRepository).findByUsername(username);
    }


    @Test
    void shouldUpdateLastLoginByEmail() {
        // Given
        String email = "test@example.com";
        LocalDateTime currentTime = LocalDateTime.now();

        // When
        userService.updateLastLoginByEmail(email, currentTime);

        // Then
        verify(userRepository).updateLastLoginByEmail(email, currentTime);
    }

    // Helper methods

    private UserDTO createTestUserDTO() {
        return UserDTO.builder()
                .username("testUser")
                .password("password")
                .name("Test")
                .surname("User")
                .email("testuser@example.com")
                .build();
    }

    private User createTestUser(UserDTO userDTO) {
        User user = new User(
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getEmail(),
                null, null, null, null
        );

        Role role = new Role(user, "ROLE_USER");
        UserDetail userDetails = new UserDetail(userDTO.getName(), userDTO.getSurname(), user);

        user.setRole(role);
        user.setUserDetail(userDetails);
        return user;
    }
}
