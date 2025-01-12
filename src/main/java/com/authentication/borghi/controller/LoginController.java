package com.authentication.borghi.controller;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class LoginController {

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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof OidcUser oidcUser){
            System.out.println("Email: " + oidcUser.getEmail());
            System.out.println("Provider id: " + oidcUser.getSubject());
            System.out.println("Provider id: " + oidcUser.getBirthdate());
            model.addAttribute("email",oidcUser.getEmail());

        }

        if (principal instanceof UserDetails userDetails){
            System.out.println("Email: " + userDetails.getUsername());
            model.addAttribute("email", userDetails.getUsername());
        }



        return "home";
    }

}
