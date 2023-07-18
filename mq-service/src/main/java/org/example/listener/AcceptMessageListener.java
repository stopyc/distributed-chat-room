package org.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.event.AcceptMessageEvent;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @program: util
 * @description: 用户接受消息监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class AcceptMessageListener {

    @Async
    @EventListener(classes = AcceptMessageEvent.class)
    public void handleEvent(AcceptMessageEvent acceptMessageEvent) {
        WsMessageVO wsMessageVO = acceptMessageEvent.getWsMessageVO();
    }
}
