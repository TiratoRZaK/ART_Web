package com.projects.ART_Web.controllers;

import com.projects.ART_Web.entities.Article;
import com.projects.ART_Web.interfaces.ArticleRepository;
import com.projects.ART_Web.interfaces.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class MainController {
    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping(value = {"/", "/home"})
    public String mainPage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        model.addAttribute("auth_user", currentUser);
        Iterable<Article> articles = articleRepository.findAll();
        model.addAttribute("articles", articles);
        return "home";
    }
}


