package com.authentication.borghi.controller;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.strategy.auth.AuthenticationHandler;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log
@Controller
public class LoginController {

    private final List<AuthenticationHandler> handlers;

    @Autowired
    public LoginController( List<AuthenticationHandler> handlers) {
        this.handlers = handlers;
    }

    @GetMapping("/showMyCustomLogin")
    public String showMyCustomLogin() {
        return "login";
    }

    @GetMapping("/showCreateAccount")
    public String showCreateAccount(@ModelAttribute("userDTO") UserDTO userDTO){
        return "createAccount";
    }

    @GetMapping("/home")
    public String showHome(Model model, Authentication authentication) {

        // Obtener el usuario autenticado
        Map<String, Object> attributes = handlers.stream()
                .filter(handler -> handler.supports(authentication.getPrincipal()))  // Filtra los handlers que soportan el principal
                .findFirst()  // Devuelve el primer handler que soporte el principal
                .map(handler -> handler.getAttributes(authentication.getPrincipal()))  // Llama a getAttributes y devuelve el mapa
                .orElse(Collections.emptyMap());


        model.addAllAttributes(attributes);
        return "home";
    }

}
