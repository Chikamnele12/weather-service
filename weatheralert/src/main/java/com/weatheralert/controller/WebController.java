package com.weatheralert.controller;

import com.weatheralert.model.User;
import com.weatheralert.model.WeatherResponse;
import com.weatheralert.repository.UserRepository;
import com.weatheralert.service.EmailService;
import com.weatheralert.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    private final WeatherService weatherService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public WebController(WeatherService weatherService,
                         UserRepository userRepository,
                         EmailService emailService) {
        this.weatherService = weatherService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute User user, Model model) {
        userRepository.save(user);

        // ✅ Send welcome email
        emailService.sendWelcomeEmail(user.getName(), user.getEmail());

        model.addAttribute("name", user.getName());
        return "success";
    }

    @GetMapping("/weather")
    public String checkWeather(@RequestParam(required = false) String city, Model model) {
        if (city != null && !city.isEmpty()) {
            var response = weatherService.getWeatherForCity(city);
            model.addAttribute("weather", response);
        }
        return "weather";
    }

    @GetMapping("/test-email")
    public String testEmailForm() {
        return "test-email";
    }
    @PostMapping("/test-email/send")
    public String sendTestEmail(
            @RequestParam String city,
            @RequestParam String email,
            Model model) {
        try {
            WeatherResponse weatherResponse = weatherService.getWeatherForCity(city);
            boolean rain = weatherService.willRain(city);

            String subject = "Weather Alert for " + city;
            String body = "Current temperature in " + city + " is "
                    + weatherResponse.getMain().getTemp() + "°C.\n";
            body += rain ? "It might rain today. Don't forget your umbrella! ☔"
                    : "The weather looks good — no rain expected today. 🌤️";

            // Send email
            weatherService.getEmailService().sendEmail(email, subject, body);

            // Add preview content to the model
            model.addAttribute("message", "✅ Test email sent successfully to " + email + " for " + city);
            model.addAttribute("emailContent", body.replaceAll("\n", "<br>"));

        } catch (Exception e) {
            model.addAttribute("message", "❌ Failed to send email: " + e.getMessage());
        }
        return "test-email";
    }

}
