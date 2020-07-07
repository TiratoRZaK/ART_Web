package com.projects.ART_Web.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Value("${hhhh}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("name", appName);
        return "home";
    }
}

