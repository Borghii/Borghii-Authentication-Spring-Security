package com.authentication.borghi.service;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.Role;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserDetail;
import com.authentication.borghi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
        assertThat(capturedUser.getUserDetail().getName()).isEqualTo(userDTO.getName());
        assertThat(capturedUser.getUserDetail().getSurname()).isEqualTo(userDTO.getSurname());
        assertThat(capturedUser.getEmail()).isEqualTo(userDTO.getEmail());
        assertThat(passwordEncoder.matches(userDTO.getPassword(), capturedUser.getPassword())).isTrue();
        assertThat(capturedUser.getRole().getRoleName()).isEqualTo("ROLE_USER");

    }

    @Test
    void shouldReturnUserDetailsWhenUserExists() {
        //GIVEN

        String username = "pedrito";

        User user = new User(username, "password", "email@example.com", null, null, new Role(), new UserDetail());
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

    @Test
    void testSaveOauthUserWithOidcUserCapturesUserDTO() throws MalformedURLException {
        // GIVEN
        OidcUser oidcUser = Mockito.mock(OidcUser.class);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(oidcUser.getSubject()).thenReturn("12345");
        when(oidcUser.getIssuer()).thenReturn(new URL("https://accounts.google.com"));
        when(oidcUser.getName()).thenReturn("John");
        when(oidcUser.getFamilyName()).thenReturn("Doe");

        // Crear un Spy de la clase bajo prueba
        UserServiceImpl spyService = Mockito.spy(new UserServiceImpl(userRepository, passwordEncoder));

        ArgumentCaptor<UserDTO> userDTOCaptor = ArgumentCaptor.forClass(UserDTO.class);

        // WHEN
        spyService.saveOauthUser(oidcUser);

        // THEN
        verify(spyService).saveUserFromDTO(userDTOCaptor.capture());
        UserDTO capturedUserDTO = userDTOCaptor.getValue();

        assertThat(capturedUserDTO).isNotNull();
        assertThat(capturedUserDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(capturedUserDTO.getProviderId()).isEqualTo("12345");
        assertThat(capturedUserDTO.getProvider()).isEqualTo("google");
        assertThat(capturedUserDTO.getName()).isEqualTo("John");
        assertThat(capturedUserDTO.getSurname()).isEqualTo("Doe");
    }

    @Test
    void testSaveOauthUserWithDefaultOAuth2UserCapturesUserDTO() throws MalformedURLException {
        //given
        Map<String,Object> attributes = new HashMap<>();

        attributes.put("id",12345);
        //attributes.put("Email",null);

        DefaultOAuth2User defaultOAuth2User = Mockito.mock(DefaultOAuth2User.class);
        when(defaultOAuth2User.getAttributes()).thenReturn(attributes);

        UserServiceImpl spyService = Mockito.spy(new UserServiceImpl(userRepository,passwordEncoder));

        ArgumentCaptor<UserDTO> userDTOArgumentCaptor = ArgumentCaptor.forClass(UserDTO.class);

        //when

        spyService.saveOauthUser(defaultOAuth2User);

        //then

        verify(spyService).saveUserFromDTO(userDTOArgumentCaptor.capture());
        UserDTO capturedUserDTO = userDTOArgumentCaptor.getValue();

        assertThat(capturedUserDTO).isNotNull();
        assertThat(capturedUserDTO.getEmail()).isEqualTo(12345+"@gmail.com");
        assertThat(capturedUserDTO.getProviderId()).isEqualTo("12345");

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