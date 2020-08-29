package com.projects.ART_Web.controllers;

import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.services.UserService;
import com.projects.ART_Web.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        userService.autoLogin(userForm.getUsername());
        return "redirect:/home";
    }

    @GetMapping(path = "/login")
    public String login(@RequestParam(name = "error", required = false) String error,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Email или пароль введены некорректно!");
        }
        model.addAttribute("message", "Для записи на консультацию нужно войти на сайт");
        return "login";
    }

    @GetMapping(path = "/activate/{code}")
    public String activateMail(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("message", "Пользователь успешно активировали аккаунт.<br> Можете войти в него.");
        } else {
            model.addAttribute("error", "Некорректный код активации.");
        }
        return "login";
    }
}
