package com.authentication.borghi.controller;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

            UserDTO userDTO = UserDTO.builder()
                            .email(oidcUser.getEmail())
                            .providerId(oidcUser.getSubject())
                            .provider(oidcUser.getIssuer().toString().split("\\.")[1])
                            .name(oidcUser.getName())
                            .surname(oidcUser.getFamilyName())
                            .username(oidcUser.getPreferredUsername())
                            .build();


            try {
                userService.saveUserFromDTO(userDTO);
            } catch (Exception ignored) {
            }


            oidcUser.getClaims().forEach((key,value)-> System.out.println(key+" : "+value));
            model.addAttribute("email",oidcUser.getEmail());

        }

        if (principal instanceof UserDetails userDetails){
            System.out.println("Email: " + userDetails.getUsername());
            model.addAttribute("email", userDetails.getUsername());
        }



        return "home";
    }

}
