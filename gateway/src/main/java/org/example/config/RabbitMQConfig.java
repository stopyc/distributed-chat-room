package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: rabbit-mq
 * @description: 消息队列配置类
 * @author: stop.yc
 * @create: 2023-02-14 17:40
 **/
@Configuration
@Slf4j
public class RabbitMQConfig {

    /**
     * 队列和交换机的名字,需要请在这里配置
     */

    public static final String EXCHANGE_NAME = "boot_topic_exchange";

    public static final String DLX_EXCHANGE_NAME = "dlx_topic_exchange";

    public static final String QUEUE_NAME = "boot_queue";

    public static final String DLX_QUEUE_NAME = "dlx_queue";

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    /**
     * 设置确认模式和回退模式(不需要改动)
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        /**
         *  回退模式 :当消息发送到Exchange后,Exchange路由到queue失败时才会执行returncallback
         *  1. 开启回退模式
         *  2. 设置returncallback
         *  3. exchange处理消息的模式
         *      1. 如果消息没有路由到queue,则丢弃消息(默认)
         *      2. 如果消息没又路由到queue,返回给消息发送方returncallback
         */

        //设置exchange处理消息的模式,如果不设置,那么exchange即使错误也不会调用返回方法!
        rabbitTemplate.setMandatory(true);

        //设置返回方法
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {

            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                String exchange1 = returnedMessage.getExchange();
                String replyText = returnedMessage.getReplyText();
                String routingKey1 = returnedMessage.getRoutingKey();
                int replyCode = returnedMessage.getReplyCode();
                String message1 = new String(returnedMessage.getMessage().getBody());
                log.error("消息发送到队列中失败!! exchange:{},replyText:{},routingKey:{},replyCode:{},message:{}"
                        , exchange1, replyText, routingKey1, replyCode, message1);

            }
        });


        /**
         * 设置回调函数,表示消息在发出去后,总是会执行的方法
         * correlationData: 配置的相关信息,可以在发送消息的时候设置,这里先为空
         * ack: 消息发送成功与否
         * cause: 失败原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                //成功
            } else {
                log.error("消息发送到交换机中失败,错误原因为: {}", cause);
            }
        });
        return rabbitTemplate;
    }




    //=======================================第一组交换机与队列=====================================
    /**
     * 交换机
     *
     * @return :交换机对象
     */
    @Bean("bootExchange")
    public Exchange bootExchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                //持久化
                .durable(true)
                .build();
    }

    /**
     * 队列
     *
     * @return :队列对象
     */
    @Bean("bootQueue")
    public Queue bootQueue() {
        //持久化,队列中的消息过期时间为10s
        return QueueBuilder
                //持久化,表示重启mq依旧数据存在
                .durable(QUEUE_NAME)
                //过期时间
                .withArgument("x-message-ttl", 600000L)
                //队列绑定死信交换机
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                //队列绑定死信队列路由,发送的路由为dlx.dead(死信队列的路由接受为dlx.#)
                .withArgument("x-dead-letter-routing-key", "dlx.dead")
                //最大长度
                .withArgument("x-max-length", 5000L)
                .build();

    }

    /**
     * 队列与交换机绑定关系,Binding
     *
     * @param bootQueue    :哪个队列
     * @param bootExchange :哪个交换机
     * @return :binding对象
     */
    @Bean
    public Binding bindQueueExchange(@Qualifier("bootQueue") Queue bootQueue, @Qualifier("bootExchange") Exchange bootExchange) {
        //设置请求到队列的路由为boot.#
        return BindingBuilder.bind(bootQueue).to(bootExchange).with("boot.#").noargs();
    }


    //=====================================死信队列=========================================

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
                .withArgument("x-message-ttl", 300000L)
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
