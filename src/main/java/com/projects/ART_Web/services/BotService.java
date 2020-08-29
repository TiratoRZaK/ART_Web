package com.projects.ART_Web.services;

import com.projects.ART_Web.bot.ART_Web_bot;
import com.projects.ART_Web.entities.*;
import com.projects.ART_Web.interfaces.NotificationRepository;
import com.projects.ART_Web.interfaces.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotService {
    private final ART_Web_bot artWebBot = ART_Web_bot.getBot();

    @Value("${chatId}")
    private String chatId;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    public Request acceptRequest(long requestId){
        Request request = requestRepository.getById(requestId);
        SendMessage answerMessage = new SendMessage();
        answerMessage.setText("Выберите точное время начала консультации по заявке #" + requestId + ":");
        answerMessage.setReplyMarkup(getKeyboardOfTimeIntervalRequest(request.getTimeInterval()));
        sendMessageInChat(answerMessage);
        return request;
    }

    public Request cancelRequest(long requestId){
        Request request = requestRepository.getById(requestId);
        SendMessage answerMessage = new SendMessage();
        answerMessage.setText("Теперь опишите причину отмены заявки #"+requestId+" после символов 'отмена заявки' и пробела");
        sendMessageInChat(answerMessage);
        return request;
    }

    public void notifyOfNewRequest(Request newRequest) {
        artWebBot.setActiveRequest(newRequest);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(getMessageForNewRequest(newRequest));
        sendMessage.setReplyMarkup(getKeyboardForNewRequest(newRequest));
        sendMessageInChat(sendMessage);
    }

    public void notifyOfCancelRequest(Request request) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Заявка #"+request.getId()+" отменена пользователем.");
        try {
            artWebBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public InlineKeyboardMarkup getKeyboardOfTimeIntervalRequest(TimeInterval interval) {
        List<List<InlineKeyboardButton>> listButtons = new ArrayList<>();
        switch (interval) {
            case first_interval: {
                List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
                buttonRow1.add(new InlineKeyboardButton().setText("08:00").setCallbackData("setTim08:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("08:30").setCallbackData("setTim08:30"));
                List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
                buttonRow2.add(new InlineKeyboardButton().setText("09:00").setCallbackData("setTim09:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("09:30").setCallbackData("setTim09:30"));
                List<InlineKeyboardButton> buttonRow3 = new ArrayList<>();
                buttonRow3.add(new InlineKeyboardButton().setText("10:00").setCallbackData("setTim10:00"));
                buttonRow3.add(new InlineKeyboardButton().setText("10:30").setCallbackData("setTim10:30"));
                List<InlineKeyboardButton> buttonRow4 = new ArrayList<>();
                buttonRow4.add(new InlineKeyboardButton().setText("11:00").setCallbackData("setTim11:00"));
                buttonRow4.add(new InlineKeyboardButton().setText("11:30").setCallbackData("setTim11:30"));
                List<InlineKeyboardButton> buttonRow5 = new ArrayList<>();
                buttonRow5.add(new InlineKeyboardButton().setText("12:00").setCallbackData("setTim12:00"));
                buttonRow5.add(new InlineKeyboardButton().setText("12:30").setCallbackData("setTim12:30"));
                listButtons.add(buttonRow1);
                listButtons.add(buttonRow2);
                listButtons.add(buttonRow3);
                listButtons.add(buttonRow4);
                listButtons.add(buttonRow5);
                break;
            }
            case second_interval: {
                List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
                buttonRow1.add(new InlineKeyboardButton().setText("13:00").setCallbackData("setTim13:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("13:30").setCallbackData("setTim13:30"));
                List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
                buttonRow2.add(new InlineKeyboardButton().setText("14:00").setCallbackData("setTim14:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("14:30").setCallbackData("setTim14:30"));
                List<InlineKeyboardButton> buttonRow3 = new ArrayList<>();
                buttonRow3.add(new InlineKeyboardButton().setText("15:00").setCallbackData("setTim15:00"));
                buttonRow3.add(new InlineKeyboardButton().setText("15:30").setCallbackData("setTim15:30"));
                List<InlineKeyboardButton> buttonRow4 = new ArrayList<>();
                buttonRow4.add(new InlineKeyboardButton().setText("16:00").setCallbackData("setTim16:00"));
                buttonRow4.add(new InlineKeyboardButton().setText("16:30").setCallbackData("setTim16:30"));
                List<InlineKeyboardButton> buttonRow5 = new ArrayList<>();
                buttonRow5.add(new InlineKeyboardButton().setText("17:00").setCallbackData("setTim17:00"));
                buttonRow5.add(new InlineKeyboardButton().setText("17:30").setCallbackData("setTim17:30"));
                listButtons.add(buttonRow1);
                listButtons.add(buttonRow2);
                listButtons.add(buttonRow3);
                listButtons.add(buttonRow4);
                listButtons.add(buttonRow5);
                break;
            }
            case three_interval: {
                List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
                buttonRow1.add(new InlineKeyboardButton().setText("18:00").setCallbackData("setTim18:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("18:30").setCallbackData("setTim18:30"));
                List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
                buttonRow2.add(new InlineKeyboardButton().setText("19:00").setCallbackData("setTim19:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("19:30").setCallbackData("setTim19:30"));
                List<InlineKeyboardButton> buttonRow3 = new ArrayList<>();
                buttonRow3.add(new InlineKeyboardButton().setText("20:00").setCallbackData("setTim20:00"));
                buttonRow3.add(new InlineKeyboardButton().setText("20:30").setCallbackData("setTim20:30"));
                List<InlineKeyboardButton> buttonRow4 = new ArrayList<>();
                buttonRow4.add(new InlineKeyboardButton().setText("21:00").setCallbackData("setTim21:00"));
                buttonRow4.add(new InlineKeyboardButton().setText("21:30").setCallbackData("setTim21:30"));
                listButtons.add(buttonRow1);
                listButtons.add(buttonRow2);
                listButtons.add(buttonRow3);
                listButtons.add(buttonRow4);
                break;
            }
            case alter_interval: {
                List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
                List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
                List<InlineKeyboardButton> buttonRow3 = new ArrayList<>();
                List<InlineKeyboardButton> buttonRow4 = new ArrayList<>();
                List<InlineKeyboardButton> buttonRow5 = new ArrayList<>();
                buttonRow1.add(new InlineKeyboardButton().setText("08:00").setCallbackData("setTim08:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("08:30").setCallbackData("setTim08:30"));
                buttonRow2.add(new InlineKeyboardButton().setText("09:00").setCallbackData("setTim09:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("09:30").setCallbackData("setTim09:30"));
                buttonRow3.add(new InlineKeyboardButton().setText("10:00").setCallbackData("setTim10:00"));
                buttonRow3.add(new InlineKeyboardButton().setText("10:30").setCallbackData("setTim10:30"));
                buttonRow4.add(new InlineKeyboardButton().setText("11:00").setCallbackData("setTim11:00"));
                buttonRow4.add(new InlineKeyboardButton().setText("11:30").setCallbackData("setTim11:30"));
                buttonRow5.add(new InlineKeyboardButton().setText("12:00").setCallbackData("setTim12:00"));
                buttonRow5.add(new InlineKeyboardButton().setText("12:30").setCallbackData("setTim12:30"));
                buttonRow1.add(new InlineKeyboardButton().setText("13:00").setCallbackData("setTim13:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("13:30").setCallbackData("setTim13:30"));
                buttonRow2.add(new InlineKeyboardButton().setText("14:00").setCallbackData("setTim14:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("14:30").setCallbackData("setTim14:30"));
                buttonRow3.add(new InlineKeyboardButton().setText("15:00").setCallbackData("setTim15:00"));
                buttonRow3.add(new InlineKeyboardButton().setText("15:30").setCallbackData("setTim15:30"));
                buttonRow4.add(new InlineKeyboardButton().setText("16:00").setCallbackData("setTim16:00"));
                buttonRow4.add(new InlineKeyboardButton().setText("16:30").setCallbackData("setTim16:30"));
                buttonRow5.add(new InlineKeyboardButton().setText("17:00").setCallbackData("setTim17:00"));
                buttonRow5.add(new InlineKeyboardButton().setText("17:30").setCallbackData("setTim17:30"));
                buttonRow1.add(new InlineKeyboardButton().setText("18:00").setCallbackData("setTim18:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("18:30").setCallbackData("setTim18:30"));
                buttonRow2.add(new InlineKeyboardButton().setText("19:00").setCallbackData("setTim19:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("19:30").setCallbackData("setTim19:30"));
                buttonRow3.add(new InlineKeyboardButton().setText("20:00").setCallbackData("setTim20:00"));
                buttonRow3.add(new InlineKeyboardButton().setText("20:30").setCallbackData("setTim20:30"));
                buttonRow4.add(new InlineKeyboardButton().setText("21:00").setCallbackData("setTim21:00"));
                buttonRow4.add(new InlineKeyboardButton().setText("21:30").setCallbackData("setTim21:30"));
                listButtons.add(buttonRow1);
                listButtons.add(buttonRow2);
                listButtons.add(buttonRow3);
                listButtons.add(buttonRow4);
                listButtons.add(buttonRow5);
                break;
            }
        }
        return new InlineKeyboardMarkup().setKeyboard(listButtons);
    }

    public InlineKeyboardMarkup getKeyboardForNewRequest(Request request) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(new InlineKeyboardButton("Принять заявку").setCallbackData("accept" + request.getId()));
        listButtons.add(row);
        row = new ArrayList<>();
        row.add(new InlineKeyboardButton("Отклонить заявку").setCallbackData("cancel" + request.getId()));
        listButtons.add(row);
        row = new ArrayList<>();
        row.add(new InlineKeyboardButton("Перенести время консультации").setCallbackData("editDt" + request.getId()));
        listButtons.add(row);
        inlineKeyboardMarkup.setKeyboard(listButtons);
        return inlineKeyboardMarkup;
    }

    public String getMessageForNewRequest(Request request) {
        return String.format("!!! НОВАЯ ЗАЯВКА !!! \n" +
                        "*** Клиент *** \n" +
                        "Попросил обращаться: %s\n" +
                        "Имя при регистрации: %s\n" +
                        "Пол: %s\n" +
                        "Email: %s\n" +
                        "Телефон: %s\n" +
                        "*** Данные для консультации *** \n" +
                        "Выбранная дата: %s\n" +
                        "Выбранный интервал времени: %s\n" +
                        "Описание проблемы: %s\n",
                request.getName(), request.getAuthor().getName(),
                request.getGender().getText(), request.getAuthor().getEmail(), request.getPhone(),
                request.getDate(), request.getTimeInterval().getText(), request.getProblem()
        );
    }

    public void sendMessageInChat(SendMessage message){
        message.setChatId(chatId);
        try {
            artWebBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendListRequests() {
        SendMessage sendMessage = new SendMessage();
        StringBuilder textMessage = new StringBuilder(String.format("Всего: %s, из них ждут вашего подтверждения: %s. \n",
                requestRepository.count(),
                requestRepository.countRequestByStatus(Status.created)));
        for (Request req : requestRepository.findAllByOrderByStatusDesc()) {
            textMessage.append(String.format("\n\n****************** ЗАЯВКА №%s ****************** \n \n" +
                            "Дата создания: %s \n" +
                            "Статус: %s \n" +
                            "Имя клиента: %s \n" +
                            "Как обращаться: %s \n" +
                            "Дата консультации: %s \n" +
                            "Интервал времени: %s \n" +
                            "Назначенное время: %s \n" +
                            "Email: %s \n" +
                            "Телефон: %s \n" +
                            "Проблема: %s \n",
                    req.getId(), req.getDate_create(), req.getStatus().getText(),
                    req.getAuthor().getName(), req.getName(), req.getDate(),
                    req.getTimeInterval().getText(), (req.getTime() == null) ? "Не назначено" : StringUtils.substring(req.getTime(), 0, 5), req.getAuthor().getEmail(),
                    req.getPhone(), req.getProblem()));
        }
        if (textMessage.length() < 55) {
            textMessage = new StringBuilder("Список заявок пуст.");
        }
        sendMessage.setText(textMessage.toString());
        sendMessageInChat(sendMessage);
    }

    public void sendListSuccessRequests() {
        SendMessage sendMessage = new SendMessage();
        StringBuilder textMessage = new StringBuilder(String.format("Всего %s подтверждённых заявок. \n",
                requestRepository.countRequestByStatus(Status.paid)));
        for (Request req : requestRepository.findAllByStatus(Status.paid)) {
            textMessage.append(String.format("\n\n****************** ЗАЯВКА №%s ****************** \n" +
                            "Дата создания: %s \n" +
                            "Имя клиента: %s \n" +
                            "Как обращаться: %s \n" +
                            "Дата консультации: %s \n" +
                            "Время консультации: %s \n" +
                            "Email: %s \n" +
                            "Телефон: %s \n" +
                            "Проблема: %s \n",
                    req.getId(), req.getDate_create(), req.getAuthor().getName(),
                    req.getName(), req.getDate(), req.getTime(),
                    req.getAuthor().getEmail(), req.getPhone(), req.getProblem()));
        }
        if (textMessage.length() < 40) {
            textMessage = new StringBuilder("Подтверждённых заявок нет.");
        }
        sendMessage.setText(textMessage.toString());
        sendMessageInChat(sendMessage);
    }

    public void saveRequest(Request request) {
        requestRepository.save(request);
    }

    public void addNewNotifyAll(String message){
        Notification notification = new Notification("pfpsp",message, TypeNotification.Common, userService.allUsers().get(1));
        notificationRepository.save(notification);
    }
}
