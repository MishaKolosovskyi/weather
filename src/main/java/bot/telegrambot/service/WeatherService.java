package bot.telegrambot.service;

import bot.telegrambot.model.Weather;
import java.io.IOException;
import java.util.List;

public interface WeatherService {
    Weather getWeatherForToday() throws IOException;
    Weather getWeatherForTomorrow() throws IOException;
    List<Weather> getWeatherForTenDays() throws IOException;
}
