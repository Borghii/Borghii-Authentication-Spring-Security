package com.authentication.borghi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/showMyCustomLogin")
    public String showMyCustomLogin(){
        return "login";
    }


}
