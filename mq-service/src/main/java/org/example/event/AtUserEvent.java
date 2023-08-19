package org.example.event;

import lombok.Getter;
import org.example.pojo.dto.MessageDTO;
import org.springframework.context.ApplicationEvent;

/**
 * @program: chat-room
 * @description: 艾特用户的事件
 * @author: stop.yc
 * @create: 2023-08-19 14:39
 **/
@Getter
public class AtUserEvent extends ApplicationEvent {

    private final MessageDTO messageDTO;

    public AtUserEvent(Object source, MessageDTO messageDTO) {
        super(source);
        this.messageDTO = messageDTO;
    }
}
