package com.projects.ART_Web.controllers;

import com.projects.ART_Web.bot.ART_Web_bot;
import com.projects.ART_Web.entities.Article;
import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.interfaces.ArticleRepository;
import com.projects.ART_Web.interfaces.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private RequestRepository requestRepository;

    @GetMapping(value = {"/", "/home"})
    public String mainPage(@AuthenticationPrincipal User activeUser, Model model) {
        model.addAttribute("activeRequest", requestRepository.findRequestByAuthor(activeUser));
        Iterable<Article> articles = articleRepository.findAll();
        model.addAttribute("articles", articles);
        ART_Web_bot.getBot();
        return "home";
    }
}


