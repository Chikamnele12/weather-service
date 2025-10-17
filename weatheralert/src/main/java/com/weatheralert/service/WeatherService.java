package com.weatheralert.service;

import com.weatheralert.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final EmailService emailService;

    public WeatherService(EmailService emailService) {
        this.emailService = emailService;
    }

    // ✅ Fetch current weather for a city
    public WeatherResponse getWeatherForCity(String city) {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city
                + "&appid=" + apiKey + "&units=metric";
        return restTemplate.getForObject(url, WeatherResponse.class);
    }

    // ✅ Check if it will rain
    public boolean willRain(String city) {
        WeatherResponse response = getWeatherForCity(city);
        return response.getWeather().stream()
                .anyMatch(w -> w.getMain().toLowerCase().contains("rain"));
    }

    // ✅ Send test HTML email with weather info
    public void sendTestEmail(String city, String email) {
        WeatherResponse weather = getWeatherForCity(city);
        double temp = weather.getMain().getTemp();
        boolean rainExpected = willRain(city);
        String condition = weather.getWeather().get(0).getMain();

        // Use OpenWeatherMap icon
        String iconUrl = "https://openweathermap.org/img/wn/"
                + (weather.getWeather().get(0).getIcon() != null
                ? weather.getWeather().get(0).getIcon()
                : "01d") + "@2x.png";

        String subject = "🌦️ Weather Alert for " + city;

        String body = """
            <html>
            <body style="font-family: 'Poppins', sans-serif; background-color: #f4f7fa; padding: 30px;">
                <div style="max-width:600px;margin:auto;background:#fff;border-radius:10px;
                            box-shadow:0 2px 6px rgba(0,0,0,0.1);padding:30px;text-align:center;">
                    <h2 style="color:#007BFF;">🌦️ Weather Alert for %s</h2>
                    <img src="%s" alt="Weather icon" style="width:100px;"/>
                    <p style="font-size:18px;color:#333;">Current Temperature: <b>%.1f°C</b></p>
                    <p style="font-size:16px;color:#555;">Condition: <b>%s</b></p>
                    %s
                    <a href="http://localhost:8080/weather?city=%s" 
                       style="display:inline-block;margin-top:20px;background-color:#007BFF;
                              color:white;text-decoration:none;padding:12px 25px;
                              border-radius:8px;font-weight:bold;">View Full Forecast</a>
                    <p style="margin-top:30px;font-size:14px;color:#777;">
                        Stay safe and weather-ready! ☂️<br>— The Weather Alert Team
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                city, iconUrl, temp, condition,
                rainExpected
                        ? "<p style='color:#e63946;font-size:16px;font-weight:bold;'>☔ It might rain today!</p>"
                        : "<p style='color:#28a745;font-size:16px;font-weight:bold;'>☀️ No rain expected today!</p>",
                city
        );

        // Send HTML email via EmailService
        emailService.sendHtmlEmail(email, subject, body);
    }

    // ✅ Getter for EmailService (optional for controller usage)
    public EmailService getEmailService() {
        return emailService;
    }
}
