package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.example.utils.PublisherUtil;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @program: chat-room
 * @description: 消息队列配置类
 * @author: stop.yc
 * @create: 2023-02-14 17:40
 **/
@Configuration
@Slf4j
@Component
public class RabbitMQConfig {


    public static final String X_MESSAGES_TTL = "x-messages-ttl";
    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String X_MAX_LENGTH = "x-max-length";

    @Resource
    private PublisherUtil publisherUtil;


    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
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
        /*
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
                /*
                    官方描述：
                    由于rabbitmq client bug导致客户端内部出现线程死锁导致消息没有进入到队列中,
                    可以通过另起一个线程来从新发送可以解决。
                 */
                if (replyCode == 312) {
                    log.info("消息发送失败，失败code为312，重新发送消息");
                    CompletableFuture.runAsync(() -> {
                        log.info("retry send message one more time when trigger ReturnCallback message");
                        rabbitTemplate.convertAndSend(exchange1, routingKey1, message1);
                    });
                }
            }
        });


        /*
         * 设置回调函数,表示消息在发出去后,总是会执行的方法
         * correlationData: 配置的相关信息,可以在发送消息的时候设置,这里先为空
         * ack: 消息发送成功与否
         * cause: 失败原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            //自定义消息确认回文
            MyMessageCorrelationData myMessageCorrelationData = (MyMessageCorrelationData) correlationData;
            //check
            if (myMessageCorrelationData == null) {
                return;
            }
            //谁发的这条消息
            //消息成功发送到交换,表示客户端的消息已经成功地发送到交换机中,这里把redis的ack队列的对应的消息ack掉,表示不需要超时重试
            if (ack) {
                //发送ack
                myMessageCorrelationData.setSuccess(true);
            } else {
                myMessageCorrelationData.setSuccess(false);
                myMessageCorrelationData.setThrowableMsg(cause);
            }
            publisherUtil.pushWsMessage2Exchange(this, myMessageCorrelationData);
        });
        return rabbitTemplate;
    }
}
