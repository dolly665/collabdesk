package com.in6206.collabdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CollabDeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollabDeskApplication.class, args);
    }
}
