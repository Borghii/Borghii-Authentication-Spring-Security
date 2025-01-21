package com.authentication.borghi.controller;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.service.UserService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Log
@Controller
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/showMyCustomLogin")
    public String showMyCustomLogin() {
        return "login";
    }

    @GetMapping("/showCreateAccount")
    public String showCreateAccount(Model model){
        model.addAttribute("userDTO", new UserDTO());
        return "createAccount";
    }

    @GetMapping("/home")
    public String showHome(Model model) {
        // Obtener el usuario autenticado
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Caso: Usuario autenticado con Google (OIDC)
        if (principal instanceof OAuth2User oAuth2User) {
            try {
                userService.saveOauthUser(oAuth2User);
            } catch (UserAlreadyExist e) {
                log.info(e.getMessage());
            }
            model.addAttribute("name", oAuth2User.getAttribute("name"));
        }

        // Caso: Usuario autenticado con detalles locales (UserDetails)
        else if (principal instanceof UserDetails userDetails) {
            System.out.println("Email: " + userDetails.getUsername());
            model.addAttribute("name", userDetails.getUsername());
        }

        return "home";
    }

}
