package org.example.event_listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.config.WsMessageMqConfig;
import org.example.constant.MessageType;
import org.example.event.PushBusinessAckEvent;
import org.example.pojo.bo.MessageBO;
import org.example.websocket.GlobalWsMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: util
 * @description: 用户接受ws消息并推送消息到队列的监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class PushBusinessAckListener {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Async
    @EventListener(classes = PushBusinessAckEvent.class)
    public void handleEvent(PushBusinessAckEvent pushBusinessAckEvent) {

        MessageBO messageBO = pushBusinessAckEvent.getMessageBO();
        messageBO.setMessageType(MessageType.UNICAST.getMessageType());
        //谁处理谁back
        if (GlobalWsMap.isOnline(messageBO.getFromUserId())) {
            rabbitTemplate.convertAndSend(WsMessageMqConfig.WS_EXCHANGE_NAME, "message.ws", JSONObject.toJSONString(messageBO));
        }
    }
}
