package org.example.event;

import lombok.Getter;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author YC104
 * @description: 推送消息到交换机的事件, 用于异步处理消息推送到交换机的结果
 */
@Getter
public class PushWsMessage2ExchangeEvent extends ApplicationEvent {

    private final MyMessageCorrelationData myMessageCorrelationData;

    private CompletableFuture<MyMessageCorrelationData> future;

    public PushWsMessage2ExchangeEvent(Object source, CompletableFuture<MyMessageCorrelationData> future, MyMessageCorrelationData myMessageCorrelationData) {
        super(source);
        this.myMessageCorrelationData = myMessageCorrelationData;
        this.future = future;
    }

    public PushWsMessage2ExchangeEvent(Object source, MyMessageCorrelationData myMessageCorrelationData) {
        super(source);
        this.myMessageCorrelationData = myMessageCorrelationData;
    }
}
