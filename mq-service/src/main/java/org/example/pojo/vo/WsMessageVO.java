package org.example.pojo.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @program: chat-room
 * @description: ws消息原始vo类
 * @author: stop.yc
 * @create: 2023-07-18 20:26
 **/
@Data
@Builder
public class WsMessageVO {
    private Integer messageType;

    private Long fromUserId;

    private String message;

    private byte[] binaryMessage;

    private Long serverTime;
}
