package org.example.event_listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.config.WsMessageMqConfig;
import org.example.constant.RedisKey;
import org.example.event.PushWsMessage2ExchangeEvent;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.exception.SystemException;
import org.example.util.RedisNewUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @program: chat-room
 * @description: 服务端推送ws消息到exchange的事件
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class PushWsMessage2ExchangeListener {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Async
    @EventListener(classes = PushWsMessage2ExchangeEvent.class)
    public void handleEvent(PushWsMessage2ExchangeEvent pushWsMessage2ExchangeEvent) {
        //如果失败,从redis持久队列中获取消息,并重试发送
        MyMessageCorrelationData correlationData = pushWsMessage2ExchangeEvent.getMyMessageCorrelationData();
        Boolean success = correlationData.getSuccess();
        if (success) {
            log.info("客户端消息id为 {} 发送到交换机中成功, 把redis中的ack队列的消息删除!", correlationData.getClientMessageId());
            RedisNewUtil.del(RedisKey.ACK_MESSAGE_KEY,
                    correlationData.getFromUserId() + ":" + correlationData.getClientMessageId());
        } else {
            log.error("客户端消息id为 {} 发送到交换机中失败,错误原因为: {}", correlationData.getClientMessageId(), correlationData.getThrowableMsg());
            Set<MessageBO> messageBoSet = RedisNewUtil.zget(RedisKey.MESSAGE_KEY, correlationData.getFromUserId(), correlationData.getClientMessageId(), MessageBO.class);
            if (CollectionUtils.isEmpty(messageBoSet) || messageBoSet.size() > 1) {
                throw new SystemException("redis中的消息队列中的客户端消息id 为 " + correlationData.getClientMessageId() + " 不止一条或者为空,请检查!");
            }
            for (MessageBO messageBo : messageBoSet) {
                RedisNewUtil.put(RedisKey.ACK_MESSAGE_KEY,
                        messageBo.getFromUserId() + ":" + messageBo.getClientMessageId(),
                        messageBo,
                        RedisKey.ACK_EXPIRATION_TIME,
                        TimeUnit.SECONDS);
                push2Mq(correlationData, messageBo);
            }
        }
    }

    private void push2Mq(MyMessageCorrelationData myMessageCorrelationData, MessageBO messageBO) {
        //发送消息, ack和nak的逻辑在配置类中.
        rabbitTemplate.convertAndSend(WsMessageMqConfig.WS_EXCHANGE_NAME, "message.ws", JSONObject.toJSONString(messageBO), myMessageCorrelationData);
    }
}
