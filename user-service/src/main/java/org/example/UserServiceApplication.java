package org.example;

import org.example.config.FeignConfiguration;
import org.example.feign.ESClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @program: cloud
 * @description: 启动类
 * @author: stop.yc
 * @create: 2023-03-19 22:18
 **/
@SpringBootApplication
@EnableCaching
@MapperScan("org.example.mapper")
@EnableFeignClients(clients = {ESClient.class}, defaultConfiguration = FeignConfiguration.class)
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
