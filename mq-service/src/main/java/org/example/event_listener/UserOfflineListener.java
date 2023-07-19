package org.example.event_listener;

import lombok.extern.slf4j.Slf4j;
import org.example.event.UserOfflineEvent;
import org.example.websocket.GlobalWsMap;
import org.example.websocket.MyWebSocket;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @program: util
 * @description: 用户下线监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class UserOfflineListener {
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void handleEvent(UserOfflineEvent userOfflineEvent) {
        MyWebSocket myWebSocket = userOfflineEvent.getMyWebSocket();
        GlobalWsMap.offline(myWebSocket);
    }
}
