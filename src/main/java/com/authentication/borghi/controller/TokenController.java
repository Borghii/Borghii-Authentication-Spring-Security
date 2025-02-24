package com.authentication.borghi.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class TokenController {

    @GetMapping("/ott/sent")
    public String sentPage() {
        return "ott-send"; // Página de confirmación
    }

}

