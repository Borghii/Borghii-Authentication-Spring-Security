package com.authentication.borghi.integration;

import static com.authentication.borghi.constants.TestConstants.*;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.user.User;
import com.authentication.borghi.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private UserDTO userDTO;

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
        mockMvc.perform(post(REGISTER_URL)
                        .with(csrf())
                        .param("username", userDTO.getUsername())
                        .param("password", userDTO.getPassword())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname())
                        .param("email", userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_URL))
                .andDo(print());

        User user = userService.findUserByUsername(userDTO.getUsername());

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userDTO.getEmail());
    }

    @Test
    void shouldThrowUserAlreadyExist() throws Exception {
        userService.saveUserFromDTO(userDTO);

        mockMvc.perform(post(REGISTER_URL)
                        .with(csrf())
                        .param("username", userDTO.getUsername())
                        .param("password", userDTO.getPassword())
                        .param("name", userDTO.getName())
                        .param("surname", userDTO.getSurname())
                        .param("email", userDTO.getEmail())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_ACCOUNT_VIEW))
                .andExpect(model().attributeExists(ALREADY_EXIST));
    }
}
