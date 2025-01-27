package com.authentication.borghi.integration;


import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.security.SecurityConfig;
import com.authentication.borghi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;


    UserDTO userDTO;

    @BeforeEach
    void setUp() {
         userDTO = UserDTO.builder()
                .username("testUser")
                .password("password")
                .name("Test")
                .surname("User")
                .email("testuser@example.com")
                .build();
    }


    @Test
    void shouldCreateUserDatabase() throws Exception {

        // when & then
        mockMvc.perform(post("/register")
                        .with(csrf()) // MUY IMPORTANTE POR ESTO NO ME FUNCIONABA
                        .param("username", userDTO.getUsername()) // Parámetros del formulario
                        .param("password", userDTO.getPassword())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname())
                        .param("email", userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)) // Tipo de contenido para formularios
                .andExpect(status().is3xxRedirection()) // Redirección (HTTP 3xx)
                .andExpect(redirectedUrl("/showMyCustomLogin")) // Verifica la redirección
                .andDo(print()); // Imprime la respuesta para depuración


        //Verify user added to the database
        User user = userService.findUserByUsername(userDTO.getUsername());

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userDTO.getEmail());

    }


    @Test
    void shouldThrowUserAlreadyExist() throws Exception {

        //given
        userService.saveUserFromDTO(userDTO);

        // when & then
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", userDTO.getUsername())
                        .param("password", userDTO.getPassword())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname())
                        .param("email", userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()) // Espera un código 200
                .andExpect(view().name("createAccount")) // Verifica que se devuelve la vista de creación de cuenta
                .andExpect(model().attributeExists("alreadyExist")); // Verifica que el atributo "alreadyExist" está en el modelo


    }


}
