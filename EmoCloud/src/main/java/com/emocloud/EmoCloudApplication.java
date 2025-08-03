package com.emocloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class EmoCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmoCloudApplication.class, args);
    }

}
