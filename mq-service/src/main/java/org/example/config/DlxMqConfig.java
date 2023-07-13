package org.example.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import static org.example.config.RabbitMQConfig.X_MESSAGES_TTL;

/**
 * @program: chat-room
 * @description: 死信队列配置
 * @author: stop.yc
 * @create: 2023-07-13 11:28
 **/
public class DlxMqConfig {

    public static final String DLX_QUEUE_NAME = "dlx_queue";

    public static final String DLX_EXCHANGE_NAME = "dlx_topic_exchange";

    //=====================================广播队列=========================================
    /**
     * 死信交换机
     *
     * @return :死信交换机对象
     */
    @Bean("dlxExchange")
    public Exchange dlxExchange() {
        return ExchangeBuilder.topicExchange(DLX_EXCHANGE_NAME).durable(true).build();
    }


    @Bean("dlxQueue")
    public Queue dixQueue() {
        //持久化,死信队列中的消息过期时间为30s
        return QueueBuilder
                .durable(DLX_QUEUE_NAME)
                .withArgument(X_MESSAGES_TTL, 30000L)
                .build();
    }


    /**
     * 死信队列与交死信换机绑定关系
     *
     * @param dlxQueue    :死信队列
     * @param dlxExchange :死信交换机
     * @return :binding对象
     */
    @Bean
    public Binding bindDLXQueueExchange(@Qualifier("dlxQueue") Queue dlxQueue, @Qualifier("dlxExchange") Exchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with("dlx.#").noargs();
    }
}
