package org.example.event;

import lombok.Getter;
import org.example.pojo.bo.MessageBO;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author YC104
 * @description: mq监听队列获取到ws发送的消息
 */
@Getter
public class ReceiveWsMessageEvent extends ApplicationEvent {

    private final MessageBO messageBO;

    private CompletableFuture<Boolean> future;

    public ReceiveWsMessageEvent(Object source, CompletableFuture<Boolean> future, MessageBO messageBO) {
        super(source);
        this.messageBO = messageBO;
        this.future = future;
    }

    public ReceiveWsMessageEvent(Object source, MessageBO messageBO) {
        super(source);
        this.messageBO = messageBO;
    }
}
