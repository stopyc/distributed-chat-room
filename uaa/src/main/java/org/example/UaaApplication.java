package org.example;

import org.example.config.FeignConfiguration;
import org.example.feign.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author YC104
 * @author YC104
 * @program: cloud
 * @description: 启动类
 * @author: stop.yc
 * @create: 2023-03-19 22:31
 **/
@SpringBootApplication
/**
 * 这个需要添加feign配置类,写在了common模块中
 * @author: stop.yc
 */
@EnableFeignClients(clients = {UserClient.class}, defaultConfiguration = FeignConfiguration.class)
public class UaaApplication {
    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class, args);
    }
}
