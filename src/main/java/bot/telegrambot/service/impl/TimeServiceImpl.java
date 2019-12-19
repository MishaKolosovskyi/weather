package bot.telegrambot.service.impl;

import bot.telegrambot.service.TimeService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeServiceImpl implements TimeService {

    @Override
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
