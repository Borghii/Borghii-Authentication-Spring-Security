package com.authentication.borghi.strategy.auth;

import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCredentialUserEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebAuthUserHandler implements AuthenticationHandler{
    @Override
    public boolean supports(Object principal) {
        return principal instanceof ImmutablePublicKeyCredentialUserEntity;
    }

    @Override
    public void handle(Object principal) {

    }

    @Override
    public Map<String, Object> getAttributes(Object principal) {
        ImmutablePublicKeyCredentialUserEntity user = (ImmutablePublicKeyCredentialUserEntity) principal;
        return Map.of("name",user.getName());
    }
}
