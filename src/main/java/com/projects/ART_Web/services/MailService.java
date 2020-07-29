package com.projects.ART_Web.services;

import com.projects.ART_Web.entities.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${application.url}")
    private String appUrl;

    public void send(String toEmail, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        mailSender.send(mailMessage);
    }

    public void sendMailSubmitRequest(String toEmail, String subject, Request request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);

        String text = String.format("Здравствуйте, %s!\n\n" +
                        "Нелли ознакомилась с вашей заявкой и готова помочь. Дело осталось за малым.\n" +
                        "Тщательно ознакомьтесь с датой, временем и местом назначенной консультации. " +
                        "При возникновении вопросов обращайтесь в месседжеры WhatsApp или Telegram по номеру" +
                        " 8 800 555 3535. \nЕсли вопросов не возникает, переходите по ссылке на сайт," +
                        " для совершения предоплаты.\n%s/request/%s \n Спасибо за обращение! \n\n\n" +
                        "Дата консультации: %s \n" +
                        "Время консультации: %s \n" +
                        "Место консультации: г. Санкт-Петербург, м.Невский проспект, ул.Казанская, 7В. " +
                        "Вход через арку и бизнес-центр Казанский.\n\n" +
                        "Для более ясного описания маршрута к письму прикреплены пояснительные картинки. \n\n",
                request.getName(),
                appUrl,
                request.getId(),
                request.getDate(),
                request.getTime());
        helper.setText(text);

        String pathToPicture = "src/main/resources/static/img/mail/map.png";
        FileSystemResource file = new FileSystemResource(new File(pathToPicture));
        helper.addAttachment("kak_dobratsaya.png", file);
        mailSender.send(message);
    }

    public void sendMailCanceledRequest(String toEmail, String subject, Request request) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);

        String text = String.format("Здравствуйте, %s!\n\n" +
                        "Нелли ознакомилась с вашей заявкой и, к сожалению, решила," +
                        " что помочь не сможет по следующей причине: \n\n%s\n\n" +
                        "Простите за потраченное время.",
                request.getName(),
                request.getCauseCancel());
        mailMessage.setText(text);

        mailSender.send(mailMessage);
    }
}