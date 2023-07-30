package org.example.push;

import org.example.pojo.bo.MessageBO;

/**
 * @author YC104
 */
public interface PushWorker {
    void push2User(MessageBO messageBO);

    void push2Group(MessageBO messageBO);

    default void push(MessageBO message) {
        if (message.getMessageType() == 6) {
            push2Group(message);
        } else if (message.getMessageType() == 7) {
            push2User(message);
        }
    }
}
