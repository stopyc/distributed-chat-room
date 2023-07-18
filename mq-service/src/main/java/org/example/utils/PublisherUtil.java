package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.event.UserOnlineEvent;
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
}
