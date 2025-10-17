package com.weatheralert.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ✅ Simple plain text email
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // ✅ Fancy HTML Welcome Email
    public void sendWelcomeEmail(String name, String to) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("🌤️ Welcome to Weather Alert, " + name + "!");

            String htmlContent = """
                <html>
                <body style="font-family: 'Poppins', sans-serif; background-color: #f7f9fc; padding: 30px;">
                    <div style="max-width: 600px; margin: auto; background: white; border-radius: 10px; 
                                box-shadow: 0 2px 6px rgba(0,0,0,0.1); padding: 30px;">
                        <div style="text-align: center;">
                            <h1 style="color: #007BFF;">🌦️ Weather Alert</h1>
                            <p style="font-size: 18px; color: #333;">Hello <b>%s</b>,</p>
                            <p style="font-size: 16px; color: #555;">
                                Thank you for registering with <b>Weather Alert Service</b>!<br>
                                You’ll now receive daily weather updates and alerts for your city.
                            </p>
                            <a href="http://localhost:8080/weather" 
                               style="display: inline-block; margin-top: 20px; background-color: #007BFF; 
                                      color: white; text-decoration: none; padding: 12px 25px; 
                                      border-radius: 8px; font-weight: bold;">
                                Check Today’s Weather
                            </a>
                            <p style="margin-top: 30px; font-size: 14px; color: #777;">
                                Stay dry, stay sunny, and have a great day!<br>
                                — The Weather Alert Team ☀️
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name);

            helper.setText(htmlContent, true); // send as HTML
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email: " + e.getMessage(), e);
        }
    }

    // ✅ General-purpose HTML email sender
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage(), e);
        }
    }
}
