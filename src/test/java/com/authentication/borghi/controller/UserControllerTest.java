package com.authentication.borghi.controller;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    BindingResult theBindingResult;

    @Test
    void shouldCreateUserAndAddSuccessMessage() throws Exception {
        // Given
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .password("password")
                .name("Test")
                .surname("User")
                .email("testuser@example.com")
                .build();

        doNothing().when(userService).saveUserFromDTO(any(UserDTO.class));

        // When / Then
        mockMvc.perform(post("/register")
                        .param("username", userDTO.getUsername())
                        .param("password", userDTO.getPassword())
                        .param("email", userDTO.getEmail())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/showMyCustomLogin"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "User created successfully! Please log in."));
    }
    @Test
    void shouldReturnCreateAccountPageWhenUserAlreadyExists() throws Exception {
        // Given
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .password("password")
                .name("Test")
                .surname("User")
                .email("testuser@example.com")
                .build();

        // Simula que el servicio lanza una excepción
        doThrow(new UserAlreadyExist("Username or email already used"))
                .when(userService).saveUserFromDTO(any(UserDTO.class));

        // When / Then
        mockMvc.perform(post("/register")
                        .param("username", userDTO.getUsername())
                        .param("password", userDTO.getPassword())
                        .param("email", userDTO.getEmail())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname()))
                .andExpect(status().isOk()) // Espera un código 200 (éxito)
                .andExpect(view().name("createAccount")) // Espera la vista "createAccount"
                .andExpect(model().attributeExists("alreadyExist")); // Espera el atributo "alreadyExist"
    }

    @Test
    void shouldReturnCreateAccountIfBindingHasErrors() throws Exception {


        //when
        when(theBindingResult.hasErrors()).thenReturn(true);

        //then
        mockMvc.perform(post("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("createAccount"));

    }
}