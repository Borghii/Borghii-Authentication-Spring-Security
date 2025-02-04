package com.authentication.borghi.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;

import static com.authentication.borghi.constants.TestConstants.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;


    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }



    @Test
    void shouldReturnLoginView() throws Exception {
        mockMvc.perform(get(LOGIN_URL).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_VIEW));
    }

    @Test
    void shouldReturnCreateAccountViewWithUserDTO() throws Exception {
        mockMvc.perform(get(CREATE_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_ACCOUNT_VIEW))
                .andExpect(model().attributeExists("userDTO"));
    }

    @Test
    void shouldShowHomeForOauth2User() throws Exception {
        setSecurityContextWithUser(Mockito.mock(OAuth2User.class));

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(HOME_VIEW));
    }


    @Test
    void shouldShowHomeForLocalUser() throws Exception {
        setSecurityContextWithUser(Mockito.mock(UserDetails.class));

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(HOME_VIEW));
    }

    /**
     * Método auxiliar para configurar el contexto de seguridad con un usuario simulado.
     */
    private void setSecurityContextWithUser(Object user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of())
        );
    }
}