package bot.telegrambot;

import bot.telegrambot.model.Weather;
import bot.telegrambot.service.TimeService;
import bot.telegrambot.service.WeatherService;
import bot.telegrambot.service.impl.TimeServiceImpl;
import bot.telegrambot.service.impl.WeatherServiceImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class MyBot extends TelegramLongPollingBot {

    private static final String WEATHER_SYMBOL = "☁";
    private static final String CURRENT_TIME_SYMBOL = "\uD83D\uDD52";
    private static final String GET_TODAY_S_WEATHER = "Get today's weather";
    private static final String GET_TOMORROW_S_WEATHER = "Get tomorrow's weather";
    private static final String GET_WEATHER_FOR_TEN_DAYS = "Get weather for ten days";
    private static final String BACK = "Back";
    private final WeatherService weatherService = new WeatherServiceImpl();
    private final TimeService timeService = new TimeServiceImpl();
    private final Logger logger = Logger.getLogger(MyBot.class);

    @Override
    public void onUpdateReceived(Update update) {
        try {
            execute(mainMenu(update.getMessage()));
        } catch (TelegramApiException | IOException e) {
            logger.error("onUpdateReceived method "  + e);
        }
    }

    @Override
    public String getBotUsername() {
        return "my_december_test_bot";
    }

    @Override
    public String getBotToken() {
        return "1010975891:AAGK42w5I3yufi3Z_2JiViP8N3RSHmrnbMs";
    }

    private ReplyKeyboardMarkup addMainMenuButton() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow currentTimeButton = new KeyboardRow();
        currentTimeButton.add(CURRENT_TIME_SYMBOL);
        KeyboardRow weatherButton = new KeyboardRow();
        weatherButton.add(WEATHER_SYMBOL);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(currentTimeButton);
        rows.add(weatherButton);
        markup.setKeyboard(rows);
        return markup;
    }

    private ReplyKeyboardMarkup addWeatherMenuButton() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow currentWeatherButton = new KeyboardRow();
        currentWeatherButton.add(GET_TODAY_S_WEATHER);
        KeyboardRow tomorrowWeatherButton = new KeyboardRow();
        tomorrowWeatherButton.add(GET_TOMORROW_S_WEATHER);
        KeyboardRow weatherForTenDaysButton = new KeyboardRow();
        weatherForTenDaysButton.add(GET_WEATHER_FOR_TEN_DAYS);
        KeyboardRow backButton = new KeyboardRow();
        backButton.add(BACK);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(currentWeatherButton);
        rows.add(tomorrowWeatherButton);
        rows.add(weatherForTenDaysButton);
        rows.add(backButton);
        markup.setKeyboard(rows);
        return markup;
    }

    private SendMessage greetings(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("How can I help you?");
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyMarkup(addMainMenuButton());
        return sendMessage;
    }

    private SendMessage getWeatherMenu(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Choose an action");
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyMarkup(addWeatherMenuButton());
        return sendMessage;
    }

    private SendMessage mainMenu(Message message) throws IOException {
        switch (message.getText()) {
            case (CURRENT_TIME_SYMBOL):
                return getCurrentTime(message);
            case (WEATHER_SYMBOL):
                return getWeatherMenu(message);
            case (GET_TODAY_S_WEATHER):
                return getWeatherForToday(message);
            case (GET_TOMORROW_S_WEATHER):
                return getWeatherForTomorrow(message);
            case (GET_WEATHER_FOR_TEN_DAYS):
                return getWeatherForTenDays(message);
            case (BACK):
                return greetings(message);
            default:
                return greetings(message);
        }
    }

    private SendMessage getCurrentTime(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(timeService.getCurrentTime());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    private SendMessage getWeatherForToday(Message message) throws IOException {
        Weather weather = weatherService.getWeatherForToday();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(weather.getDay() + " "
                + weather.getNumber() + " "
                + weather.getMonth() + "\n"
                + weather.getMinTemperature() + " "
                + weather.getMaxTemperature() + "\n"
                + weather.getDescription());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    private SendMessage getWeatherForTomorrow(Message message) throws IOException {
        Weather weather = weatherService.getWeatherForTomorrow();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(weather.getDay() + " "
                + weather.getNumber() + " "
                + weather.getMonth() + "\n"
                + weather.getMinTemperature() + " "
                + weather.getMaxTemperature() + "\n"
                + weather.getDescription());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    private SendMessage getWeatherForTenDays(Message message) throws IOException {
        List<Weather> weathers = weatherService.getWeatherForTenDays();
        SendMessage sendMessage = new SendMessage();
        String response = "";
        for (int counter = 0; counter <= 9; counter++) {
            response = response.concat(weathers.get(counter).getDay())
                    .concat(" ")
                    .concat(weathers.get(counter).getNumber())
                    .concat(" ")
                    .concat(weathers.get(counter).getMonth())
                    .concat("\n")
                    .concat(weathers.get(counter).getMinTemperature())
                    .concat(" ")
                    .concat(weathers.get(counter).getMaxTemperature())
                    .concat("\n")
                    .concat(weathers.get(counter).getDescription())
                    .concat("\n")
                    .concat("\n");
        }
        sendMessage.setText(response);
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }
}
