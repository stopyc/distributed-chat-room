package org.example.config;

import org.example.interceptor.InnerHeaderRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YC104
 * @description: feign远程调用配置
 */
@Configuration
public class FeignConfiguration {

    @Bean
    public InnerHeaderRequestInterceptor innerHeaderRequestInterceptor() {
        return new InnerHeaderRequestInterceptor();
    }
}