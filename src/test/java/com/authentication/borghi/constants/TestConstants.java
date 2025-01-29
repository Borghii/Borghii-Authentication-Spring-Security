package com.authentication.borghi.constants;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class TestConstants {

    //USER CONSTANTS

    public static final OidcUser TEST_OIDC_USER = new OidcUser() {
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

    };

    public static final UserDetails TEST_USER_DETAILS = new org.springframework.security.core.userdetails.User(
            "tomi",
            "123",
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

    // URLs
    public static final String REGISTER_URL = "/register";
    public static final String LOGIN_URL = "/showMyCustomLogin";
    public static final String CREATE_ACCOUNT_URL = "/showCreateAccount";
    public static final String HOME_URL = "/home";
    public static final String ACCESS_DENIED_URL = "/access-denied";

    // Vistas
    public static final String LOGIN_VIEW = "login";
    public static final String CREATE_ACCOUNT_VIEW = "createAccount";
    public static final String HOME_VIEW = "home";
    public static final String ACCESS_DENIED_VIEW = "access-denied";

    // Mensajes
    public static final String SUCCESS_MESSAGE = "User created successfully! Please log in.";
    public static final String ERROR_MESSAGE = "Username or email already used";

    // Parámetros de usuario
    public static final String TEST_USERNAME = "testUser";
    public static final String TEST_PASSWORD = "password";
    public static final String TEST_EMAIL = "testuser@example.com";
    public static final String TEST_NAME = "Test";
    public static final String TEST_SURNAME = "User";

    // ATTRIBUTES MODEL

    public static final String ALREADY_EXIST = "alreadyExist";

    private TestConstants() {
        throw new IllegalStateException("Utility class");
    }

}
