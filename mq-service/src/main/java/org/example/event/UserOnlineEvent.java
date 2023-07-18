package org.example.event;

import lombok.Getter;
import org.example.pojo.bo.UserBO;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author YC104
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private final UserBO user;

    private CompletableFuture<UserBO> future;

    public UserOnlineEvent(Object source, CompletableFuture<UserBO> future, UserBO user) {
        super(source);
        this.user = user;
        this.future = future;
    }

    public UserOnlineEvent(Object source, UserBO user) {
        super(source);
        this.user = user;
    }
}
