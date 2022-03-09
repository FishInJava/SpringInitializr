package com.happyzombie.springinitializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringInitializrApplication {

    public static void main(String[] args) {
        // 请求  -  生成一个excel进行简单筛选
        SpringApplication.run(SpringInitializrApplication.class, args);
    }

}
