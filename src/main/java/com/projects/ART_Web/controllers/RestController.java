package com.projects.ART_Web.controllers;

import com.projects.ART_Web.entities.Notification;
import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.interfaces.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @Autowired
    private NotificationRepository notificationRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/notifyMe")
    public List<Notification> getNotifications(@AuthenticationPrincipal User user, Model model){
        return notificationRepository.getAllByOwnerId(user.getId());
    }
}
