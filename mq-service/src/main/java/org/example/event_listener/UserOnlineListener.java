package org.example.event_listener;

import lombok.extern.slf4j.Slf4j;
import org.example.event.UserOnlineEvent;
import org.example.websocket.GlobalWsMap;
import org.example.websocket.MyWebSocket;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @program: util
 * @description: 用户上线监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class UserOnlineListener {

    //@Async
    @EventListener(classes = UserOnlineEvent.class)
    public void handleEvent(UserOnlineEvent userOnlineEvent) {
        MyWebSocket myWebSocket = userOnlineEvent.getMyWebSocket();
        GlobalWsMap.online(myWebSocket);
    }
}
