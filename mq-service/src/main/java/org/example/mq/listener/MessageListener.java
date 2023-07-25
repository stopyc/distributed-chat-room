package org.example.mq.listener;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.config.WsMessageMqConfig;
import org.example.event.ReceiveWsMessageEvent;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.utils.MessageAckUtil;
import org.example.websocket.GlobalWsMap;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.example.config.WsMessageMqConfig.WS_EXCHANGE_NAME;


/**
 * @program: chat-room
 * @description: 消息队列监听器
 * @author: stop.yc
 * @create: 2023-02-14 21:14
 **/
@Component
@Slf4j
public class MessageListener {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 监听ws_fanout_exchange交换机,失败进行进入死信队列进行重试
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(), //切记： 此处无需设置队列名称，否在得话，多个消费者只有一个消费者能消费数据。其它消费者无法消费数据。
            exchange = @Exchange(value = WS_EXCHANGE_NAME, type = ExchangeTypes.FANOUT)
    ))
    public void onMessage(Message message, Channel channel) throws Exception {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        boolean isSuccessful = false;

        try {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            MessageBO messageBO = JSONObject.parseObject(msg, MessageBO.class);
            // 1.接受消息
            log.info("交换机 {} 接受到消息: {}", WS_EXCHANGE_NAME, messageBO);
            //Business ack
            boolean success = doBusiness(messageBO);

            if (success) {
                channel.basicAck(deliveryTag, true);
            } else {
                log.info("广播队列 {} 重试后依旧失败,进入死信队列", WsMessageMqConfig.WS_QUEUE_NAME);
                channel.basicNack(deliveryTag, true, false);
            }

            //防止业务处理的方法未能捕获业务异常
        } catch (Exception e) {
            //未知异常!
            log.error("队列 {} 业务执行过程中发生了未知错误!", WsMessageMqConfig.WS_QUEUE_NAME, e);
        }
    }

    /**
     * 执行正常队列的业务处理
     */
    private boolean doBusiness(MessageBO messageBO) {
        try {
            if (messageBO.getMessageType() == 1) {
                MessageDTO businessMessageAck = MessageAckUtil.getBusinessMessageAck(messageBO);
                GlobalWsMap.sendText(messageBO.getFromUserId(), businessMessageAck);
                return true;
            } else {
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                eventPublisher.publishEvent(new ReceiveWsMessageEvent(this, future, messageBO));
                CompletableFuture.allOf(future);
                return future.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
