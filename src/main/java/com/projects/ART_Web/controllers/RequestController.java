package com.projects.ART_Web.controllers;

import com.projects.ART_Web.bot.ART_Web_bot;
import com.projects.ART_Web.entities.Request;
import com.projects.ART_Web.entities.Status;
import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.interfaces.RequestRepository;
import com.projects.ART_Web.validators.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class RequestController {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestValidator requestValidator;
    private BindingResult validateErrors;

    @GetMapping(path = "/request")
    public String addRequest(@RequestParam(name = "confirm", required = false) boolean confirm, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("request", new Request(user));
        if (validateErrors != null && validateErrors.hasErrors()) {
            model.addAttribute("errors", validateErrors);
        }
        if (confirm) {
            model.addAttribute("confirm", "Ваша заявка успешно отправлена! \nОжидайте ответа по электронной почте или звонка на указанный номер!");
        } else {
            model.addAttribute("confirm", "");
        }
        Date dateNow = new Date();
        LocalDateTime localDateTime = dateNow.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        model.addAttribute("dateMin", new SimpleDateFormat("yyyy-MM-dd").format(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())));
        model.addAttribute("dateMax", new SimpleDateFormat("yyyy-MM-dd").format(Date.from(localDateTime.plusWeeks(2).atZone(ZoneId.systemDefault()).toInstant())));

        return "request";
    }

    @PostMapping(path = "/request")
    public String addRequest(@ModelAttribute(name = "request") @Valid Request request, @AuthenticationPrincipal User user, BindingResult errors, Model model) {
        request.setAuthor(user);
        requestValidator.validate(request, errors);
        if (errors.hasErrors()) {
            validateErrors = errors;
            return "redirect:/request";
        }
        requestRepository.save(request);
        ART_Web_bot.getBot().notifyOfNewRequest(request);
        return "redirect:/request?confirm=true";
    }

    @GetMapping(path = "/request/{id}")
    public String addRequest(@PathVariable(name = "id", required=true) int id, @AuthenticationPrincipal User user , Model model) {
        model.addAttribute("request", requestRepository.findRequestByAuthor(user));
        return "myRequest";
    }
}
