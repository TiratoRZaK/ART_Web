package com.projects.ART_Web.bot;

import com.projects.ART_Web.entities.Request;
import com.projects.ART_Web.entities.Status;
import com.projects.ART_Web.entities.TimeInterval;
import com.projects.ART_Web.interfaces.RequestRepository;
import com.projects.ART_Web.services.BotService;
import com.projects.ART_Web.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ART_Web_bot extends TelegramLongPollingBot {
    static {
        ApiContextInitializer.init();
    }

    @Autowired
    private BotService botService;

    @Autowired
    private EmailService emailService;

    private static ART_Web_bot artWebBot;

    private Request activeRequest;

    private ART_Web_bot() {
    }

    public static ART_Web_bot getBot() {
        if (artWebBot != null) {
            return artWebBot;
        }
        ART_Web_bot bot = new ART_Web_bot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        artWebBot = bot;
        return artWebBot;
    }

    public void setActiveRequest(Request request) {
        activeRequest = request;
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "Список заявок": {
                    botService.sendListRequests();
                    break;
                }
                case "Подтверждённые заявки": {
                    botService.sendListSuccessRequests();
                    break;
                }
                default: {
                    if (activeRequest != null && message.getText().startsWith("отмена заявки")) {
                        activeRequest.setStatus(Status.canceled);
                        activeRequest.setCauseCancel(message.getText().substring(14));
                        botService.saveRequest(activeRequest);
                        emailService.sendToEmailCanceledRequest(activeRequest.getAuthor().getEmail(), "АРТ-Терапия. Отмена заявки.", activeRequest);
                        sendMsg(message, "Заявка успешно отменена.");
                        activeRequest = null;
                    } else {
                        if (message.getText().startsWith("всем")) {
                            botService.addNewNotifyAll(message.getText().substring(5));
                        } else {
                            sendMsg(message, "Простите, я не знаю как вам ответить :(");
                        }
                    }
                }
            }
        } else {
            if (update.hasCallbackQuery()) {
                String dataCallback = update.getCallbackQuery().getData();
                if (dataCallback.startsWith("accept")) {
                    long requestId = Long.parseLong(dataCallback.substring(6));
                    setActiveRequest(botService.acceptRequest(requestId));
                } else {
                    if (dataCallback.startsWith("cancel")) {
                        long requestId = Long.parseLong(dataCallback.substring(6));
                        setActiveRequest(botService.cancelRequest(requestId));
                    } else {
                        if (dataCallback.startsWith("setTim")) {
                            activeRequest.setTime(dataCallback.substring(6));
                            activeRequest.setStatus(Status.accepted);
                            botService.saveRequest(activeRequest);
                            try {
                                sendMsg(update.getCallbackQuery().getMessage(), "Заявка #" + activeRequest.getId() + " успешно отправлена на предоплату!");
                                emailService.sendToEmailSubmitRequest(activeRequest.getAuthor().getEmail(), "АРТ-Терапия. Ваша заявка принята", activeRequest);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }
                }
            }
        }
    }


    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Список заявок"));
        keyboardFirstRow.add(new KeyboardButton("Подтверждённые заявки"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public String getBotUsername() {
        return "ArtTherapyWeb_bot";
    }

    public String getBotToken() {
        return "1396369444:AAGYLpwWAUJNL2sTOH-0IZK4ek6lYfJ8sQE";
    }
}
