package bot.telegrambot.service.impl;

import bot.telegrambot.service.WeatherService;
import bot.telegrambot.model.Weather;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherServiceImpl implements WeatherService {

    private static final String URL = "https://sinoptik.ua/%D0%BF%D0%BE%D0%B3%D" +
            "0%BE%D0%B4%D0%B0-%D0%BA%D0%B8%D0%B5%D0%B2/10-%D0%B4%D0%BD%D0%B5%D0%B9";
    private static final String WEATHER_CLASS = "div[class=tabs]";
    private static final String BASIC_HTTP = "https:";
    private static final String FIRST_DAY_CLASS = "div[id=bd1]";
    private static final String SECOND_DAY_CLASS = "div[id=bd2]";
    private static final String CLASS_DESCRIPTION = "div[class=description]";
    private static final String DAY_LINK = "a[class=day-link]";
    private static final String HREF = "href";

    private Document getDocument(String url) throws IOException {
        return Jsoup.parse(new URL(url), 3000);
    }

    @Override
    public Weather getWeatherForToday() throws IOException {
        Document page = getDocument(URL);
        Elements pageData = page.select(WEATHER_CLASS);
        String[] dayData = pageData.select(FIRST_DAY_CLASS)
                .text()
                .split(" ");
        Weather weather = new Weather();
        weather.setDay(dayData[0]);
        weather.setNumber(dayData[1]);
        weather.setMonth(dayData[2]);
        weather.setMinTemperature(dayData[3] + dayData[4]);
        weather.setMaxTemperature(dayData[5] + dayData[6]);
        weather.setDescription(page.select(CLASS_DESCRIPTION)
                .first()
                .text());
        return weather;
    }

    @Override
    public Weather getWeatherForTomorrow() throws IOException {
        Document page = getDocument(URL);
        Elements pageData = page.select(WEATHER_CLASS);
        String[] dayData = pageData.select(SECOND_DAY_CLASS)
                .text()
                .split(" ");
        Weather weather = new Weather();
        weather.setDay(dayData[0]);
        weather.setNumber(dayData[1]);
        weather.setMonth(dayData[2]);
        weather.setMinTemperature(dayData[3] + dayData[4]);
        weather.setMaxTemperature(dayData[5] + dayData[6]);
        String tomorrowWeatherUrl = pageData.select(SECOND_DAY_CLASS)
                .select(DAY_LINK)
                .first()
                .attributes()
                .get(HREF);
        Document tomorrowPage = getDocument(BASIC_HTTP + tomorrowWeatherUrl);
        weather.setDescription(tomorrowPage.select(CLASS_DESCRIPTION)
                .first()
                .text());
        return weather;
    }

    @Override
    public List<Weather> getWeatherForTenDays() throws IOException {
        List<Weather> weatherForTenDays = new ArrayList<>();
        Document page = getDocument(URL);
        Elements pageData = page.select(WEATHER_CLASS);
        for (int dayNumberCounter = 1; dayNumberCounter <= 10; dayNumberCounter++) {
            String[] dayData = pageData.select("div[id=bd" + dayNumberCounter + "]")
                    .text()
                    .split(" ");
            Weather weather = new Weather();
            weather.setDay(dayData[0]);
            weather.setNumber(dayData[1]);
            weather.setMonth(dayData[2]);
            weather.setMinTemperature(dayData[3] + dayData[4]);
            weather.setMaxTemperature(dayData[5] + dayData[6]);
            if (dayNumberCounter == 1) {
                weather.setDescription(page.select(CLASS_DESCRIPTION)
                        .first()
                        .text());
            } else {
                String nextDayUrl = pageData.select("div[id=bd" + dayNumberCounter + "]")
                        .select(DAY_LINK)
                        .first()
                        .attributes()
                        .get(HREF);
                Document tomorrowPage = getDocument(BASIC_HTTP + nextDayUrl);
                weather.setDescription(tomorrowPage.select(CLASS_DESCRIPTION)
                        .first()
                        .text());
            }
            weatherForTenDays.add(weather);
        }
        return weatherForTenDays;
    }
}
