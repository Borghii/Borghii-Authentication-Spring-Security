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




    @Test
    void testHandlerOauth2WorksPerfectly() throws ServletException, IOException {

        //given
        successHandler.setHandlers(new ArrayList<>(List.of(oAuth2UserHandler)));

        Authentication authentication = new TestingAuthenticationToken(createUserOauth2(), null);

        //when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        //then

        assertThatThrownBy(() -> {
            userService.saveOauthUser((OAuth2User) authentication.getPrincipal());
        })
                .isInstanceOf(UserAlreadyExist.class)
                .hasMessage("Username or email already used");
    }




    private OAuth2User createUserOauth2(){
        return new OidcUser() {
            @Override
            public String getName() {
                return "tomi";
            }

            @Override
            public Map<String, Object> getAttributes() {
                return Map.of("name","tomi");
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public Map<String, Object> getClaims() {
                return Map.of();
            }

            @Override
            public OidcUserInfo getUserInfo() {
                return null;
            }

            @Override
            public OidcIdToken getIdToken() {
                return null;
            }

            @Override
            public String getEmail() {
                return "johndoe@example.com";
            }

            @Override
            public String getSubject() {
                return OidcUser.super.getSubject();
            }

            @Override
            public URL getIssuer() {
                try {
                    return new URL("http://google.com");
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

        } ;
    }



}
