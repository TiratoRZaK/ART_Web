package com.projects.ART_Web;

import com.projects.ART_Web.bot.ART_Web_bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@SpringBootApplication
public class ArtWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArtWebApplication.class, args);
    }
}
