package com.authentication.borghi.controller;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
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
        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/showMyCustomLogin"))
                .andExpect(status().isOk()) // Verifica que el estado HTTP sea 200
                .andExpect(view().name("login")); // Verifica que el nombre de la vista sea "login"
    }

    @Test
    void shouldReturnCreateAccountViewWithUserDTO() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/showCreateAccount"))
                .andExpect(status().isOk()) // Verifica que el estado HTTP sea 200 (OK)
                .andExpect(view().name("createAccount")) // Verifica que la vista sea "createAccount"
                .andExpect(model().attributeExists("userDTO")); // Verifica que el modelo contiene "userDTO"
    }

    @Test
    void shouldShowHomeForOauth2User() throws Exception {

        // Simular un usuario con detalles locales
        OAuth2User oAuth2User = Mockito.mock(OAuth2User.class);


        // Configurar el contexto de seguridad con el usuario simulado
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(oAuth2User, null, List.of())
        );


        // Realizar la prueba
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(status().isOk()) // Verifica que el estado HTTP sea 200
                .andExpect(view().name("home")); // Verifica que la vista sea

    }

    @Test
    void shouldShowHomeForLocalUser() throws Exception {
        // Simular un usuario con detalles locales
        UserDetails userDetails = Mockito.mock(UserDetails.class);


        // Configurar el contexto de seguridad con el usuario simulado
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, List.of())
        );


        // Realizar la prueba
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(status().isOk()) // Verifica que el estado HTTP sea 200
                .andExpect(view().name("home")); // Verifica que la vista sea

    }
}