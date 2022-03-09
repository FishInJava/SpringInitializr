package com.happyzombie.springinitializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringInitializrApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringInitializrApplication.class, args);
    }

}
