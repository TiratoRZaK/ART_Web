package com.projects.ART_Web.controllers;

import com.projects.ART_Web.entities.Request;
import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.interfaces.RequestRepository;
import com.projects.ART_Web.services.BotService;
import com.projects.ART_Web.validators.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Controller
@RequestMapping(value = "/request/")
public class RequestController {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BotService botService;

    @Autowired
    private RequestValidator requestValidator;
    private BindingResult validateErrors;

    @GetMapping(path = "/new")
    public String addRequest(@RequestParam(name = "confirm", required = false) boolean confirm, @AuthenticationPrincipal User user, Model model) {
        if(requestRepository.existsByAuthor(user)){
            return "redirect:/request/my";
        }

        model.addAttribute("request", new Request(user));
        if (validateErrors != null && validateErrors.hasErrors()) {
            model.addAttribute("errors", validateErrors);
        }
        if (confirm) {
            model.addAttribute("confirm", "Ваша заявка успешно отправлена! \n Ожидайте ответа по электронной почте или звонка на указанный номер!");
        } else {
            model.addAttribute("confirm", "");
        }
        Date dateNow = new Date();
        LocalDateTime localDateTime = dateNow.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        model.addAttribute("dateMin", new SimpleDateFormat("yyyy-MM-dd").format(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())));
        model.addAttribute("dateMax", new SimpleDateFormat("yyyy-MM-dd").format(Date.from(localDateTime.plusWeeks(2).atZone(ZoneId.systemDefault()).toInstant())));

        return "/request/new";
    }

    @PostMapping(path = "/new")
    public String addRequest(@ModelAttribute(name = "request") @Valid Request request, @AuthenticationPrincipal User user, BindingResult errors, Model model) {
        request.setAuthor(user);
        requestValidator.validate(request, errors);
        if (errors.hasErrors()) {
            validateErrors = errors;
            return "/request/new";
        }
        requestRepository.save(request);
        botService.notifyOfNewRequest(request);
        return "/request/my";
    }

    @GetMapping(path = "/my")
    public String addRequest(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("request", requestRepository.findRequestByAuthor(user));
        return "/request/my";
    }

    @GetMapping(path = "/cancel")
    public String cancel(@AuthenticationPrincipal User user) {
        Request request = requestRepository.findRequestByAuthor(user);
        botService.notifyOfCancelRequest(request);
        requestRepository.deleteById(request.getId());
        return "redirect:/home";
    }
}
