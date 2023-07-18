package org.example.pojo.vo.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pojo.vo.MessageContent;

/**
 * @program: chat-room
 * @description: 文本消息
 * @author: stop.yc
 * @create: 2023-07-18 16:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextMessageContent implements MessageContent {

    private String content;

    @Override
    public MessageContent getMessageContent() {
        return this;
    }
}
