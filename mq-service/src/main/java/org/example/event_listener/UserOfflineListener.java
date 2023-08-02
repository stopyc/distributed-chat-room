package org.example.event_listener;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.UserDao;
import org.example.event.UserOfflineEvent;
import org.example.websocket.GlobalWsMap;
import org.example.websocket.MyWebSocket;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: util
 * @description: 用户下线监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class UserOfflineListener {

    @Resource
    private UserDao userDao;

    //@Async
    @EventListener(classes = UserOfflineEvent.class)
    public void handleEvent(UserOfflineEvent userOfflineEvent) {
        MyWebSocket myWebSocket = userOfflineEvent.getMyWebSocket();
        GlobalWsMap.offline(myWebSocket);
    }

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void updateCacheStatus(UserOfflineEvent userOfflineEvent) {
        MyWebSocket myWebSocket = userOfflineEvent.getMyWebSocket();
        if (myWebSocket.getUserId() == null || myWebSocket.getUserBO() == null || myWebSocket.getSession() == null) {
            return;
        }
        userDao.removeCacheUser(myWebSocket);
    }
}
