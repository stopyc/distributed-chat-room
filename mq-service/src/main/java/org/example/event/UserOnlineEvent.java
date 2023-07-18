package org.example.event;

import lombok.Getter;
import org.example.websocket.MyWebSocket;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author YC104
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {

    private final MyWebSocket myWebSocket;

    private CompletableFuture<MyWebSocket> future;

    public UserOnlineEvent(Object source, CompletableFuture<MyWebSocket> future, MyWebSocket myWebSocket) {
        super(source);
        this.myWebSocket = myWebSocket;
        this.future = future;
    }

    public UserOnlineEvent(Object source, MyWebSocket myWebSocket) {
        super(source);
        this.myWebSocket = myWebSocket;
    }
}
