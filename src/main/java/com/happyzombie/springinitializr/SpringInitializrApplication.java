package com.happyzombie.springinitializr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan({"com.happyzombie.springinitializr.dao"})
public class SpringInitializrApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringInitializrApplication.class, args);
    }

}
