package com.authentication.borghi.integration;


import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.security.handler.CustomAuthenticationSuccessHandler;
import com.authentication.borghi.service.UserService;
import com.authentication.borghi.strategy.auth.AuthenticationHandler;
import com.authentication.borghi.strategy.auth.OAuth2UserHandler;
import com.authentication.borghi.strategy.auth.UserDetailsHandler;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

import static com.authentication.borghi.constants.TestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class HandlerAuthIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private OAuth2UserHandler oAuth2UserHandler;

    @Autowired
    private UserDetailsHandler userDetailsHandler;

    @MockitoBean  // Mock de las dependencias HTTP
    private HttpServletRequest request;

    @MockitoBean
    private HttpServletResponse response;


    @BeforeEach
    void setUp() {
        successHandler.setHandlers(List.of(oAuth2UserHandler));
    }


    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() throws ServletException, IOException {
        // Given: Un usuario OAuth2 autenticado
        Authentication authentication = new TestingAuthenticationToken(TEST_OIDC_USER, null);

        // When & Then: Se espera que falle porque el usuario ya existe
        assertThatThrownBy(() -> {
            successHandler.onAuthenticationSuccess(request, response, authentication);
            userService.saveOauthUser((OidcUser) authentication.getPrincipal());
        })
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessage(ERROR_MESSAGE);
    }







}



