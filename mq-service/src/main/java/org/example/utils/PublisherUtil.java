package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.event.*;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.vo.WsMessageVO;
import org.example.websocket.MyWebSocket;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @program: chat-room
 * @description: 事件发布工具类
 * @author: stop.yc
 * @create: 2023-07-18 15:41
 **/
@Component
@Slf4j
public class PublisherUtil {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    public void userOnline(Object source, MyWebSocket myWebSocket) {
        eventPublisher.publishEvent(new UserOnlineEvent(source, myWebSocket));
    }

    public void userOffline(Object source, MyWebSocket myWebSocket) {
        eventPublisher.publishEvent(new UserOfflineEvent(source, myWebSocket));
    }

    public void acceptMessage(Object source, WsMessageVO wsMessageVO) {
        eventPublisher.publishEvent(new PushWsMessageEvent(source, wsMessageVO));
    }

    public void receiveWsMessage(Object source, CompletableFuture<Boolean> future, MessageBO messageBO) {
        eventPublisher.publishEvent(new ReceiveWsMessageEvent(source, future, messageBO));
    }

    public void pushWsMessage2Exchange(Object source, MyMessageCorrelationData myMessageCorrelationData) {
        eventPublisher.publishEvent(new PushWsMessage2ExchangeEvent(source, myMessageCorrelationData));
    }

    public void pushBusinessAck(Object source, MessageBO messageBO) {
        eventPublisher.publishEvent(new PushBusinessAckEvent(source, messageBO));
    }
}
