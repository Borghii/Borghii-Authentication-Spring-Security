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

@Controller
@RequestMapping("/register")
public class UserController {

    private final UserService userService;

    //utilizado para interceptar todos los textos y inputs para sacarles espacios en
    // blancos y si esta vacio que lo tome como NULL
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             BindingResult theBindingResult, Model model){

        if (theBindingResult.hasErrors()) {
            return "createAccount";
        }


        try {
            userService.saveUserFromDTO(userDTO);
        } catch (UserAlreadyExist e) {
            model.addAttribute("alreadyExist",e.getMessage());
            return "createAccount";
        }

        return "redirect:/showMyCustomLogin?success";


    }



}
