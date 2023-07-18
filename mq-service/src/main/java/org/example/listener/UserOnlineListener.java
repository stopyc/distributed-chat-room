package org.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.event.UserOnlineEvent;
import org.example.pojo.bo.UserBO;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void handleEvent(UserOnlineEvent userOnlineEvent) {
        try {
            // 处理事件逻辑
            // ...
            UserBO user = userOnlineEvent.getUser();
            log.info("user 为: {}", user);
            int i = 1 / 0;
            // 返回结果
            userOnlineEvent.getFuture().complete(user);
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常
            userOnlineEvent.getFuture().completeExceptionally(e);
        }
    }
}
