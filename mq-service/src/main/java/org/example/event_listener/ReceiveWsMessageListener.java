package org.example.event_listener;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.event.ReceiveWsMessageEvent;
import org.example.pojo.bo.MessageBO;
import org.example.push.PushWorker;
import org.example.utils.PublisherUtil;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
    private PushWorker pushWorker;

    private static PublisherUtil publisherUtil;

    static {
        ReceiveWsMessageListener.publisherUtil = SpringUtil.getBean(PublisherUtil.class);
    }

    @EventListener(classes = ReceiveWsMessageEvent.class)
    public void handleEvent(ReceiveWsMessageEvent receiveWsMessageEvent) {
        try {
            //1. 查看这条消息是单发还是群发
            MessageBO messageBO = receiveWsMessageEvent.getMessageBO();
            //2. 调用推送消息代理层进行推送
            pushWorker.push(messageBO);
            //3. 发送业务ack
            publisherUtil.pushBusinessAck(this, messageBO);
            receiveWsMessageEvent.getFuture().complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            receiveWsMessageEvent.getFuture().complete(false);
        }
    }
}
