package org.example.event_listener;

import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageBO2MessageDTO;
import org.example.event.PushBusinessAckEvent;
import org.example.event.ReceiveWsMessageEvent;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.websocket.GlobalWsMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @program: chat-room
 * @description: 服务端接受到ws发送的消息
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class ReceiveWsMessageListener {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ApplicationEventPublisher eventPublisher;


    @EventListener(classes = ReceiveWsMessageEvent.class)
    public void handleEvent(ReceiveWsMessageEvent receiveWsMessageEvent) {
        try {
            //1. 查看这条消息是单发还是群发
            MessageBO messageBO = receiveWsMessageEvent.getMessageBO();
            Integer messageType = messageBO.getMessageType();
            //2. 单发的话查看对应的人是否在这个服务器上
            if (messageType == 7) {
                MessageDTO messageDTO = MessageBO2MessageDTO.getMessageDTO(messageBO, 7);
                GlobalWsMap.sendText(messageBO.getToUserId(), messageDTO);
            } else if (messageType == 6) {
                //3. 群发直接调用进行发送即可
                MessageDTO messageDTO = MessageBO2MessageDTO.getMessageDTO(messageBO, 6);
                Set<Long> userIdSet = MessageBO2MessageDTO.getUserIdSetByChatRoomId(messageDTO.getChatRoomId());
                GlobalWsMap.sendText(userIdSet, messageDTO, messageBO.getFromUserId());
            }
            //4. 发送业务ack
            eventPublisher.publishEvent(new PushBusinessAckEvent(this, messageBO));
            receiveWsMessageEvent.getFuture().complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            receiveWsMessageEvent.getFuture().complete(false);
        }
    }
}
