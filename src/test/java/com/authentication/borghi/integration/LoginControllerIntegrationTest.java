package com.authentication.borghi.integration;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Test
    void testShowMyCustomLogin() throws Exception {
        // Realiza una solicitud GET a "/showMyCustomLogin"
        mockMvc.perform(get("/showMyCustomLogin"))
                .andExpect(status().isOk()) // Verifica que el estado de la respuesta sea OK (200)
                .andExpect(view().name("login")); // Verifica que la vista devuelta sea "login"
    }

    // Test para el caso de un usuario autenticado con detalles locales (UserDetails)
    @Test
    void testShowHome_LocalUser() throws Exception {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "tomi",
                "123",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))) ;


        // Simula la autenticación con UserDetails en el SecurityContext
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "123", userDetails.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);

        // Realiza la solicitud GET a "/home" con el contexto de seguridad configurado
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk()) // Verifica que la respuesta sea OK (200)
                .andExpect(view().name("home")) // Verifica que la vista devuelta sea "home"
                .andExpect(model().attribute("name", "tomi")); // Verifica que el email esté en el modelo
    }

    // Test para el caso de un usuario autenticado con OIDC (OpenID Connect)
    @Test
    void testShowHomeOidcUser() throws Exception {
        // Crear un OidcUser manualmente (simulando un usuario de Google u otro proveedor)
        OidcUser oidcUser = new OidcUser() {
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

        // Configurar el SecurityContext con el OidcUser simulado
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new OAuth2AuthenticationToken(oidcUser, oidcUser.getAuthorities(), "google"));
        SecurityContextHolder.setContext(securityContext);

        // Realizar la solicitud GET a "/home" con el contexto de seguridad configurado
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk()) // Verifica que la respuesta sea OK (200)
                .andExpect(view().name("home")) // Verifica que la vista devuelta sea "home"
                .andExpect(model().attribute("name", "tomi"));


    }



}
