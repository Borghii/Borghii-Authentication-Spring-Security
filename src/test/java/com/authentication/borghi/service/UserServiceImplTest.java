package com.authentication.borghi.service;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.Role;
import com.authentication.borghi.entity.User;
import com.authentication.borghi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(userRepository,passwordEncoder);
    }

    @Test
    void canSaveUserFromDTO() {
        //given
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .password("password")
                .name("Test")
                .surname("User")
                .email("testuser@example.com")
                .build();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        //when

        underTest.saveUserFromDTO(userDTO);

        //then

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getUsername()).isEqualTo(userDTO.getUsername());
        assertThat(capturedUser.getName()).isEqualTo(userDTO.getName());
        assertThat(capturedUser.getSurname()).isEqualTo(userDTO.getSurname());
        assertThat(capturedUser.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(passwordEncoder.matches(userDTO.getPassword(), capturedUser.getPassword())).isTrue();
        assertThat(capturedUser.getRole().getRoleName()).isEqualTo("ROLE_USER");

    }

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        //GIVEN

        String username = "pedrito";
        User user = new User(username,"123","pedro","lopez","pdrito@gmail.com",new Role());
        Role role = new Role(user,"ROLE_USER");
        user.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //WHEN

        UserDetails userDetails = underTest.loadUserByUsername(username);

        //THEN

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));

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

}