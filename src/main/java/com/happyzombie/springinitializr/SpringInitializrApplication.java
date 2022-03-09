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
        // 请求  -  生成一个excel进行简单筛选
        SpringApplication.run(SpringInitializrApplication.class, args);
    }

}
