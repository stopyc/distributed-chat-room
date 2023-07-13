package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import static org.example.config.DlxMqConfig.DLX_EXCHANGE_NAME;
import static org.example.config.RabbitMQConfig.*;

/**
 * @program: chat-room
 * @description: Ws消息通知队列配置
 * @author: stop.yc
 * @create: 2023-07-13 11:28
 **/
public class WsMessageMqConfig {

    public static final String WS_EXCHANGE_NAME = "ws_fanout_exchange";

    public static final String WS_QUEUE_NAME = "ws_queue";

    //=====================================广播队列=========================================
    @Bean("wsQueue")
    public Queue messageQueue() {
        return QueueBuilder
                //持久化
                .durable(WS_QUEUE_NAME)
                //消息过期时间
                .withArgument(X_MESSAGES_TTL, 600000L)
                //队列绑定死信交换机
                .withArgument(X_DEAD_LETTER_EXCHANGE, DLX_EXCHANGE_NAME)
                //队列绑定死信队列路由,发送的路由为dlx.dead(死信队列的路由接受为dlx.#)
                .withArgument(X_DEAD_LETTER_ROUTING_KEY, "dlx.dead")
                //最大长度
                .withArgument(X_MAX_LENGTH, 5000L)
                //build
                .build();
    }

    @Bean("wsFanoutExchange")
    public Exchange messageExchange() {
        return ExchangeBuilder
                .fanoutExchange(WS_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Binding bindMessageQueueExchange(@Qualifier("wsQueue") Queue messageQueue, @Qualifier("wsFanoutExchange") Exchange messageExchange) {
        return BindingBuilder
                .bind(messageQueue)
                .to(messageExchange)
                .with("message.#")
                .noargs();
    }
}
