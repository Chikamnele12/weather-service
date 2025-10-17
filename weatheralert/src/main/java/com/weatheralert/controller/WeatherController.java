package com.weatheralert.controller;

import com.weatheralert.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // ✅ 1. Get current weather data for a city
    @GetMapping("/{city}")
    public ResponseEntity<?> getWeather(@PathVariable String city) {
        var weather = weatherService.getWeatherForCity(city);
        return ResponseEntity.ok(weather);
    }

    // ✅ 2. Check if rain is expected today
    @GetMapping("/{city}/rain")
    public ResponseEntity<String> willRain(@PathVariable String city) {
        boolean rain = weatherService.willRain(city);
        return ResponseEntity.ok(
                rain ? "Rain expected today in " + city + ". ☔"
                        : "No rain expected today in " + city + ". 🌤️"
        );
    }

    // ✅ 3. Send test email manually via Postman
    @GetMapping("/test-email/{city}")
    public ResponseEntity<String> sendTestEmail(
            @PathVariable String city,
            @RequestParam String email) {

        weatherService.sendTestEmail(city, email);
        return ResponseEntity.ok("✅ Hello Mr Favour! Test email has been sent successfully to "
                + email + " for city " + city + ".");
    }

}
