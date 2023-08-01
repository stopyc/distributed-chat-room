package org.example.push;

import org.example.constant.MessageType;
import org.example.pojo.bo.MessageBO;

/**
 * @author YC104
 */
public interface PushWorker {
    void push2User(MessageBO messageBO);

    void push2Group(MessageBO messageBO);

    default void push(MessageBO message) {
        if (MessageType.isChatGroup(message.getMessageType())) {
            push2Group(message);
        } else if (MessageType.isSingleChat(message.getMessageType())) {
            push2User(message);
        }
    }
}
