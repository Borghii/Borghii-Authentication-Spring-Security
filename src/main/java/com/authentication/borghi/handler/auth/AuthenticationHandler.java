package com.authentication.borghi.handler.auth;

import java.util.Map;

public interface AuthenticationHandler {
    boolean supports(Object principal);
    void handle(Object principal);
    Map<String, Object> getAttributes(Object principal);
}
