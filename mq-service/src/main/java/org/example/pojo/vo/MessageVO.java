package org.example.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: chat-room
 * @description: ws中消息的dto
 * @author: stop.yc
 * @create: 2023-04-20 12:21
 **/
@Data
public abstract class MessageVO implements Serializable {

    private Long messageId;

    /**
     * 消费类型：广播类型、单播类型、请求类型、响应类型、心跳类型、断开连接类型、
     */
    private Integer messageType;

    /**
     * 客户端时间戳,可能会快或者慢
     */
    private Long clientTime;

    /**
     * 服务器时间
     */
    private Long serverTime;

    /**
     * 发送者的id
     */
    private Long fromUserId;

    /**
     * 消息内容
     */
    private MessageContent messageContent;
}
