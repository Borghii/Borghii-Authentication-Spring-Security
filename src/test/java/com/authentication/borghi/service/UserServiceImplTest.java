package com.authentication.borghi.service;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.Role;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserDetail;
import com.authentication.borghi.entity.user.UserMapper;
import com.authentication.borghi.repository.UserRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;



    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(userRepository,passwordEncoder,userMapper);
    }

    @Test
    void canSaveUserFromDTO() {
        // Given
        UserDTO userDTO = createTestUserDTO();
        User user = createTestUser(userDTO);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userMapper.fromDTO(userDTO)).thenReturn(user);

        // When
        underTest.saveUserFromDTO(userDTO);

        // Then
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(capturedUser.getUserDetail().getName()).isEqualTo(userDTO.getName());
        assertThat(capturedUser.getUserDetail().getSurname()).isEqualTo(userDTO.getSurname());
        assertThat(capturedUser.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(passwordEncoder.matches(userDTO.getPassword(), capturedUser.getPassword())).isTrue();
        assertThat(capturedUser.getRole().getRoleName()).isEqualTo("ROLE_USER");
    }

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



    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserIsNotFound() {
        // GIVEN
        String username = "not_exist";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // THEN
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> underTest.loadUserByUsername(username)
        );

        assertEquals("User not found with username: not_exist", exception.getMessage());

        // VERIFY
        verify(userRepository).findByUsername(username);

    }




    @Test
    void shouldReturnUserWhenUsernameExists() {
        // GIVEN
        String username = "testUser";
        User user = new User(username, "password", "email@example.com", null, null, new Role(), new UserDetail());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // WHEN
        User result = underTest.findUserByUsername(username);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void shouldThrowExceptionWhenUsernameDoesNotExist() {
        // GIVEN
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // WHEN & THEN
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> underTest.findUserByUsername(username)
        );

        assertThat(exception.getMessage()).isEqualTo("User not found with username: " + username);
        verify(userRepository).findByUsername(username);
    }


    @Test
    void shouldUpdateLastLoginByEmail() {
        // GIVEN
        String email = "test@example.com";
        LocalDateTime currentTime = LocalDateTime.now();

        // No se necesita configurar el repositorio porque no devuelve valores aquí

        // WHEN
        underTest.updateLastLoginByEmail(email, currentTime);

        // THEN
        verify(userRepository).updateLastLoginByEmail(email, currentTime);
    }

}