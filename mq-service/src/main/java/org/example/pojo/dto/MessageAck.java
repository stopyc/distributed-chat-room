package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: chat-room
 * @description: 消息确认报文
 * @author: stop.yc
 * @create: 2023-07-20 10:25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageAck {
    private Long clientMessageId;

    private Long fromUserId;

    private Long serverTime;

    private Long clientTime;

    private Boolean ack;
}
