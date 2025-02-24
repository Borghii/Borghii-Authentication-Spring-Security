package com.authentication.borghi.security.handler;


import com.authentication.borghi.strategy.auth.AuthenticationHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.*;

@Setter
@Log
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private List<AuthenticationHandler> handlers = new ArrayList<>();

    public CustomAuthenticationSuccessHandler() {
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();

        handlers.stream()
                .filter(handler -> handler.supports(principal))
                .forEach(handler -> handler.handle(principal));

        response.sendRedirect("/home");

    }
}

