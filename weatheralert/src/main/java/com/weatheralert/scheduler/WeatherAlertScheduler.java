package com.weatheralert.scheduler;

import com.weatheralert.model.User;
import com.weatheralert.repository.UserRepository;
import com.weatheralert.service.WeatherService;
import com.weatheralert.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeatherAlertScheduler {

    private final UserRepository userRepository;
    private final WeatherService weatherService;
    private final EmailService emailService;

    public WeatherAlertScheduler(UserRepository userRepository, WeatherService weatherService, EmailService emailService) {
        this.userRepository = userRepository;
        this.weatherService = weatherService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 7 * * *") // Runs every day at 7 AM
    public void sendDailyAlerts() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            boolean rainExpected = weatherService.willRain(user.getCity());
            if (rainExpected) {
                String subject = "Rain Alert: Don't forget your umbrella!";
                String body = "Hello " + user.getName() + ",\n\n" +
                        "Rain is expected today in " + user.getCity() + ".\n" +
                        "Make sure to carry an umbrella! ☔\n\n" +
                        "WeatherAlert Service";

                emailService.sendEmail(user.getEmail(), subject, body);
                System.out.println("Email sent to: " + user.getEmail());
            } else {
                System.out.println("No rain expected today in " + user.getCity() + ". No email sent.");
            }
        }
    }
}
