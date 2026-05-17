package de.thb.crazyhorseracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CrazyHorseRacingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrazyHorseRacingApplication.class, args);
    }
}
