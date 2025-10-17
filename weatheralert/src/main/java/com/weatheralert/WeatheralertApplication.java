package com.weatheralert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class
WeatheralertApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeatheralertApplication.class, args);
	}
}
