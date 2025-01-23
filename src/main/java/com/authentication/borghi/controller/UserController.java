package com.authentication.borghi.controller;


import com.authentication.borghi.dto.UserDTO;
import com.authentication.borghi.exceptions.UserAlreadyExist;
import com.authentication.borghi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //utilizado para interceptar todos los textos y inputs para sacarles espacios en
    // blancos y si esta vacio que lo tome como NULL
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }



    @PostMapping
    public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult theBindingResult, Model model,
                             RedirectAttributes redirectAttributes){

        if (theBindingResult.hasErrors()) {
            return "createAccount";
        }

        try {
            userService.saveUserFromDTO(userDTO);
        } catch (UserAlreadyExist e) {
            model.addAttribute("alreadyExist",e.getMessage());
            return "createAccount";
        }

        redirectAttributes.addFlashAttribute("successMessage", "User created successfully! Please log in.");

        return "redirect:/showMyCustomLogin";
    }
}
