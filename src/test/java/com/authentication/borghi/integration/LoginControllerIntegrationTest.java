package com.authentication.borghi.integration;

import static com.authentication.borghi.constants.TestConstants.*;
import com.authentication.borghi.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldShowCustomLogin() throws Exception {
        mockMvc.perform(get(LOGIN_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_VIEW));
    }

    @Test
    void shouldShowHomeForLocalUser() throws Exception {
        authenticateLocalUser();
        performHomeRequest().andExpect(model().attribute("name", "tomi"));
    }

    @Test
    void shouldShowHomeForOidcUser() throws Exception {
        authenticateOidcUser();
        performHomeRequest().andExpect(model().attribute("name", "tomi"));
    }

    private void authenticateLocalUser() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(TEST_USER_DETAILS, "123", TEST_USER_DETAILS.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    private void authenticateOidcUser() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new OAuth2AuthenticationToken(TEST_OIDC_USER, TEST_OIDC_USER.getAuthorities(), "google"));
        SecurityContextHolder.setContext(context);
    }

    private ResultActions performHomeRequest() throws Exception {
        return mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(HOME_VIEW));
    }
}
