package com.authentication.borghi.controller;

import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


}
