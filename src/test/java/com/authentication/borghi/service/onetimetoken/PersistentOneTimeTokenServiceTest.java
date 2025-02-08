package com.authentication.borghi.service.onetimetoken;

import com.authentication.borghi.constants.TestConstants;
import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.onetimetoken.OneTimeToken;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.entity.user.UserMapper;
import com.authentication.borghi.repository.OneTimeTokenRepository;
import com.authentication.borghi.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;

// Importa las clases de tu dominio, por ejemplo:
// import com.tu.proyecto.model.User;
// import com.tu.proyecto.model.OneTimeToken;
// import com.tu.proyecto.request.GenerateOneTimeTokenRequest;
// import com.tu.proyecto.security.ott.DefaultOneTimeToken;
// import com.tu.proyecto.security.ott.OneTimeTokenAuthenticationToken;
// import com.tu.proyecto.service.UserService;
// import com.tu.proyecto.repository.OneTimeTokenRepository;
// import com.tu.proyecto.service.PersistentOneTimeTokenService;

@ExtendWith(MockitoExtension.class)
public class PersistentOneTimeTokenServiceTest {

    @Mock
    private OneTimeTokenRepository tokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PersistentOneTimeTokenService tokenService;

    private User user ;

    @BeforeEach
    void setUp() {
        user = UserMapper.fromDTO(TestConstants.TEST_USER_DTO);
    }

    // 1. Test para la generación de token
    @Test
    public void testGenerateToken() {

        //GIVEN
        GenerateOneTimeTokenRequest OTTRequest = new GenerateOneTimeTokenRequest(user.getUsername());

        when(userService.findUserByUsername(user.getUsername())).thenReturn(user);

        //WHEN

        org.springframework.security.authentication.ott.OneTimeToken generatedToken = tokenService.generate(OTTRequest);

        //THEN

        assertNotNull(generatedToken);
        assertThat(user.getUsername()).isEqualTo(generatedToken.getUsername());
        assertNotNull(generatedToken.getTokenValue());

        // Verificamos que se haya guardado el token en el repositorio
        ArgumentCaptor<OneTimeToken> tokenCaptor = ArgumentCaptor.forClass(OneTimeToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        OneTimeToken savedToken = tokenCaptor.getValue();
        assertEquals(user.getUsername(), savedToken.getUser().getUsername());
        assertFalse(savedToken.isUsed());
        // Verificamos que la fecha de expiración sea posterior a la de creación
        assertTrue(savedToken.getExpiresAt().isAfter(savedToken.getCreatedAt()));

    }

    // 2. Test para consumir un token válido
    @Test
    public void testConsumeToken() {
        // GIVEN
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        OneTimeToken token = new OneTimeToken();
        token.setTokenValue(tokenValue);
        token.setUser(user);
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusMinutes(15));
        token.setUsed(false);

        ArgumentCaptor<OneTimeToken> tokenCaptor = ArgumentCaptor.forClass(OneTimeToken.class);

        when(tokenRepository.findByTokenValueAndUsedFalse(tokenValue)).thenReturn(Optional.of(token));

        OneTimeTokenAuthenticationToken authenticationToken = new OneTimeTokenAuthenticationToken(tokenValue);

        // WHEN
        org.springframework.security.authentication.ott.OneTimeToken consumedToken = tokenService.consume(authenticationToken);

        // THEN

        assertThat(consumedToken).isNotNull();
        assertThat(consumedToken.getTokenValue()).isEqualTo(tokenValue);
        assertThat(consumedToken.getUsername()).isEqualTo(user.getUsername());

        // Verificamos que el token haya sido marcado como usado
        verify(tokenRepository).save(tokenCaptor.capture());
        OneTimeToken updatedToken = tokenCaptor.getValue();
        assertThat(updatedToken.isUsed()).isTrue();
    }

    // 3. Test para consumir un token expirado
    @Test
    public void testConsumeExpiredToken() {
        // given
        String tokenValue = "expired-token";
        OneTimeTokenAuthenticationToken authToken = new OneTimeTokenAuthenticationToken(tokenValue);

        OneTimeToken token = new OneTimeToken();
        token.setTokenValue(tokenValue);
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now().minusMinutes(20));
        token.setExpiresAt(LocalDateTime.now().minusMinutes(5)); // Token expirado
        token.setUsed(false);

        when(tokenRepository.findByTokenValueAndUsedFalse(tokenValue)).thenReturn(Optional.of(token));

        // Act & Assert: Se espera que se lance BadCredentialsException al consumir un token expirado
        assertThrows(BadCredentialsException.class, () -> tokenService.consume(authToken));
        verify(tokenRepository, never()).save(any());
    }

    @Test
    public void testRemoveExpiredTokens() {
        // GIVEN
        LocalDateTime now = LocalDateTime.now();

        // WHEN
        tokenService.removeExpiredTokens();

        // THEN
        verify(tokenRepository).deleteByExpiresAtBefore(now);
    }





}