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
public class WsMessageDTO {

    /**
     * 消息
     */
    private String message;

    /**
     * 语音消息
     */
    private byte[] bytes;

    /**
     * 用户名字
     */
    private String username;

    /**
     * 该条消息的发送者的id
     */
    private Long userId;

    /**
     * 接受消息的人的用户id
     */
    private Long getNoticeUserId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 0为语音,1为文字,2为ping
     */
    private Integer messageType;

    /**
     * 发言时间
     */
    private String timeStr;
}
