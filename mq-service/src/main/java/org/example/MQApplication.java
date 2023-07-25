package org.example;

import org.example.config.FeignConfiguration;
import org.example.feign.IdClient;
import org.example.feign.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @program: monitor
 * @description: 启动类
 * @author: stop.yc
 * @create: 2023-04-06 13:20
 **/
@SpringBootApplication
@EnableFeignClients(clients = {IdClient.class, UserClient.class}, defaultConfiguration = FeignConfiguration.class)
public class MQApplication {
    public static void main(String[] args) {
        SpringApplication.run(MQApplication.class, args);
    }
}
