package org.example.pojo.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: chat-room
 * @description: ws中消息的dto
 * @author: stop.yc
 * @create: 2023-04-20 12:21
 **/
@Data
public class MessageBO implements Serializable {

    private Long messageId;


    private Long clientMessageId;

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
     * 消息内容text
     */
    private String message;

    /**
     * 消息内容byte[]
     */
    private byte[] byteArray;

    /**
     * 群聊id
     */
    private Long chatRoomId;

    /**
     * 是文本还是二进制数据,这里目前使用Base64编码
     */
    private Boolean isText;

    /**
     * 接收者的id
     */
    private Long toUserId;
}
