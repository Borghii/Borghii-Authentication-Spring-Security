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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof OidcUser oidcUser){


            try {
                userService.saveOauthUser(oidcUser);
            } catch (UserAlreadyExist e) {
                log.info(e.getMessage());
            }


//            oidcUser.getClaims().forEach((key,value)-> System.out.println(key+" : "+value));
            model.addAttribute("email",oidcUser.getEmail());

        }

        if (principal instanceof UserDetails userDetails){
            System.out.println("Email: " + userDetails.getUsername());
            model.addAttribute("email", userDetails.getUsername());
        }



        return "home";
    }

}
