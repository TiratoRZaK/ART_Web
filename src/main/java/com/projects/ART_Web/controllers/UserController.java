package com.projects.ART_Web.controllers;

import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.services.UserService;
import com.projects.ART_Web.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserValidator userValidator;
    private BindingResult validateErrors;

    @GetMapping(path = "/register")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        if (validateErrors != null && validateErrors.hasErrors()) {
            model.addAttribute("errors", validateErrors);
        }
        return "register";
    }

    @PostMapping(path = "/register")
    public String registration(@ModelAttribute("userForm") @Valid User userForm, BindingResult errors, Model model) {
        userValidator.validate(userForm, errors);
        if (errors.hasErrors()) {
            validateErrors = errors;
            return "redirect:/register";
        }
        userService.saveUser(userForm);
        return "redirect:/";
    }

    @GetMapping(path = "/login")
    public String login(@RequestParam(name = "error", required = false) String error,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Email или пароль введены некорректно!");
        }
        return "login";
    }
}
