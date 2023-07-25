package org.example.config;

import org.example.redis_listener.RedisListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author YC104
 * @description: Redis过期事件监听配置类
 */
@Configuration
public class RedisListenerConfig {
    @Autowired
    private RedisListener redisListener;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        //监听2号库的过期事件
        container.addMessageListener(redisListener, new PatternTopic("__keyevent@2__:expired"));
        return container;
    }
}