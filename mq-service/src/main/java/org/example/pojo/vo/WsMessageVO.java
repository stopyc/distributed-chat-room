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

    /**
     * 是文本还是二进制数据,这里目前使用Base64编码
     */
    private Boolean isText;

    private String message;

    private byte[] byteArray;

    /**
     * 客户端时间戳,可能会快或者慢
     */
    private Long clientTime;

    /**
     * 群聊id
     */
    private Long chatRoomId;

    /**
     * 接收者的id
     */
    private Long toUserId;
}
