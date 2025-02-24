package com.authentication.borghi.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public void handleUserNotFoundException(UsernameNotFoundException e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        //return "redirect:/showMyCustomLogin"; // Redirige a una página de error
        request.getSession().setAttribute("usernameNotFound", e.getMessage());
        response.sendRedirect("/showMyCustomLogin");
    }
}
