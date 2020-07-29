package com.projects.ART_Web.bot;

import com.projects.ART_Web.entities.Request;
import com.projects.ART_Web.entities.Status;
import com.projects.ART_Web.entities.TimeInterval;
import com.projects.ART_Web.entities.User;
import com.projects.ART_Web.interfaces.RequestRepository;
import com.projects.ART_Web.services.MailService;
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

    @Value("${chatId}")
    private String chatId;

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private MailService mailService;

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

    public void notifyOfNewRequest(Request newRequest) {
        setActiveRequest(newRequest);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(getMessageForNewRequest(newRequest));
        sendMessage.setReplyMarkup(getKeyboardForNewRequest(newRequest));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getMessageForNewRequest(Request request) {
        String textMessage = String.format("!!! НОВАЯ ЗАЯВКА !!! \n" +
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
        return textMessage;
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
            StringBuilder text;
            switch (message.getText()) {
                case "Список заявок": {
                    text = new StringBuilder(String.format("Всего %s заявки, из них ждут ответа %s. \n",
                            requestRepository.count(),
                            requestRepository.countRequestByStatus(Status.created)));
                    for (Request req : requestRepository.findAllByOrderByStatusDesc()) {
                        text.append(String.format("\n\n\\*\\*\\*\\*\\*\\*\\*\\*\\* ЗАЯВКА №%s \\*\\*\\*\\*\\*\\*\\*\\*\\* \n" +
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
                    if (text.length() < 25) {
                        text = new StringBuilder("Список заявок пуст.");
                    }
                    sendMsg(message, text.toString());
                    break;
                }
                case "Подтверждённые заявки": {
                    text = new StringBuilder(String.format("Всего %s подтверждённых заявки. \n",
                            requestRepository.countRequestByStatus(Status.paid)));
                    for (Request req : requestRepository.findAllByStatus(Status.paid)) {
                        text.append(String.format("\n\n\\*\\*\\*\\*\\*\\*\\*\\*\\* ЗАЯВКА №%s \\*\\*\\*\\*\\*\\*\\*\\*\\* \n" +
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
                    if (text.length() < 25) {
                        text = new StringBuilder("Подтверждённых заявок нет.");
                    }
                    sendMsg(message, text.toString());
                    break;
                }
                default: {
                    if (activeRequest != null && message.getText().startsWith("отмена заявки")) {
                        activeRequest.setStatus(Status.canceled);
                        activeRequest.setCauseCancel(message.getText().substring(14));
                        requestRepository.save(activeRequest);
                        mailService.sendMailCanceledRequest(activeRequest.getAuthor().getEmail(), "АРТ-Терапия. Отмена заявки.", activeRequest);
                        sendMsg(message, "Заявка успешно отменена.");
                        activeRequest =null;
                    } else {
                        sendMsg(message, "Простите, я не знаю как вам ответить :(");
                    }
                }
            }
        } else {
            if (update.hasCallbackQuery()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                String dataCallback = update.getCallbackQuery().getData();
                String requestId = dataCallback.substring(6);
                activeRequest = requestRepository.getById(Long.valueOf(requestId));
                if (dataCallback.startsWith("accept")) {
                    sendMessage.setText("Выберите точное время начала консультации по заявке #" + activeRequest.getId() + ":");
                    sendMessage.setReplyMarkup(getKeyboardOfTimeIntervalRequest(activeRequest.getTimeInterval()));
                } else {
                    if (dataCallback.startsWith("cancel")) {
                        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery()
                                .setCallbackQueryId(update.getCallbackQuery().getId())
                                .setCacheTime(5)
                                .setShowAlert(false)
                                .setText("Теперь опишите причину отмены заявки после символов 'отмена заявки' и пробела");
                        try {
                            execute(answerCallbackQuery);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        return;
                    } else {
                        if (dataCallback.startsWith("setTim")) {
                            activeRequest.setTime(dataCallback.substring(6));
                            activeRequest.setStatus(Status.accepted);
                            requestRepository.save(activeRequest);
                            try {
                                mailService.sendMailSubmitRequest(activeRequest.getAuthor().getEmail(), "АРТ-Терапия. Ваша заявка принята", activeRequest);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                            sendMessage.setText("Заявка #" + activeRequest.getId() + " успешно отправлена на предоплату!");
                        } else {

                        }
                    }
                }
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public InlineKeyboardMarkup getKeyboardOfTimeIntervalRequest(TimeInterval interval) {
        List<List<InlineKeyboardButton>> listButtons = new ArrayList<>();
        switch (interval) {
            case first_interval: {
                List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
                buttonRow1.add(new InlineKeyboardButton().setText("8:00").setCallbackData("setTim8:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("8:30").setCallbackData("setTim8:30"));
                List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
                buttonRow2.add(new InlineKeyboardButton().setText("9:00").setCallbackData("setTim9:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("9:30").setCallbackData("setTim9:30"));
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
                buttonRow1.add(new InlineKeyboardButton().setText("8:00").setCallbackData("setTim8:00"));
                buttonRow1.add(new InlineKeyboardButton().setText("8:30").setCallbackData("setTim8:30"));
                buttonRow2.add(new InlineKeyboardButton().setText("9:00").setCallbackData("setTim9:00"));
                buttonRow2.add(new InlineKeyboardButton().setText("9:30").setCallbackData("setTim9:30"));
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
