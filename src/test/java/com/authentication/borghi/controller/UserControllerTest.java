package com.authentication.borghi.controller;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static com.authentication.borghi.constants.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private BindingResult bindingResult;

    @Test
    void shouldCreateUserAndAddSuccessMessage() throws Exception {
        // Given
        UserDTO userDTO = buildUserDTO();
        doNothing().when(userService).saveUserFromDTO(any(UserDTO.class));

        // When / Then
        mockMvc.perform(post(REGISTER_URL)
                        .param("username", userDTO.getUsername())
                        .param("password", userDTO.getPassword())
                        .param("email", userDTO.getEmail())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", SUCCESS_MESSAGE));
    }

    @Test
    void shouldReturnCreateAccountPageWhenUserAlreadyExists() throws Exception {
        // Given
        UserDTO userDTO = buildUserDTO();
        doThrow(new UserAlreadyExist(ERROR_MESSAGE)).when(userService).saveUserFromDTO(any(UserDTO.class));

        // When / Then
        mockMvc.perform(post(REGISTER_URL)
                        .param("username", userDTO.getUsername())
                        .param("password", userDTO.getPassword())
                        .param("email", userDTO.getEmail())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname()))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_ACCOUNT_VIEW))
                .andExpect(model().attributeExists("alreadyExist"));
    }

    @Test void shouldReturnCreateAccountIfBindingHasErrors() throws Exception {
        // Given
        when(bindingResult.hasErrors()).thenReturn(true);

        // When / Then
        mockMvc.perform(post(REGISTER_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_ACCOUNT_VIEW));
    }

    /**
     * Método auxiliar para construir un UserDTO de prueba.
     */
    private UserDTO buildUserDTO() {
        return UserDTO.builder()
                .username("testUser")
                .password("password")
                .name("Test")
                .surname("User")
                .email("testuser@example.com")
                .build();
    }
}
