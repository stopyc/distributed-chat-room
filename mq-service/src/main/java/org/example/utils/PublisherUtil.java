package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.event.AcceptMessageEvent;
import org.example.event.UserOfflineEvent;
import org.example.event.UserOnlineEvent;
import org.example.pojo.vo.WsMessageVO;
import org.example.websocket.MyWebSocket;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
        eventPublisher.publishEvent(new AcceptMessageEvent(source, wsMessageVO));
    }
}
