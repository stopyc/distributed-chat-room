package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: chat-room
 * @description: ws中消息的dto
 * @author: stop.yc
 * @create: 2023-04-20 12:21
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class MessageDTO {

    private Long messageId;

    private Long clientMessageId;

    /**
     * 消费类型：广播类型0、单播类型1、请求类型2、响应类型3、心跳类型4、断开连接类型5、群聊6、单聊7
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

    private String fromUserName;

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

    private String toUserName;

    private Boolean ack;

    private String color;

    private String icon;
}
