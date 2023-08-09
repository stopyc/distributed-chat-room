package org.example.pojo.vo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.pojo.AbstractMessage;
import org.example.util.Assert;
import org.example.websocket.MyWebSocket;

/**
 * @program: chat-room
 * @description: ws消息原始vo类
 * @author: stop.yc
 * @create: 2023-07-18 20:26
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class WsMessageVO extends AbstractMessage implements Cloneable {
    @Override
    public WsMessageVO clone() throws CloneNotSupportedException {
        return (WsMessageVO) super.clone();
    }

    private Long clientMessageId;

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
     * 单聊下接收者的id
     */
    private Long toUserId;

    private MyWebSocket myWebSocket;


    public void validate() {
        Assert.assertNotNull(this.messageType, "消息类型不能为空");
        Assert.assertNotNull(this.clientMessageId, "客户端消息ID不能为空");
        Assert.assertNotNull(this.clientTime, "客户端时间戳不能为空");
        if (this.messageType == 6) {
            Assert.assertNotNull(this.chatRoomId, "群聊ID不能为空");
        } else if (this.messageType == 7) {
            Assert.assertNotNull(this.toUserId, "接收者ID不能为空");
            Assert.assertNotNull(this.fromUserId, "发送者ID不能为空");
        }
    }
}
