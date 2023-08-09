package org.example.pojo;

import lombok.Data;

/**
 * @program: chat-room
 * @description: 抽象信息父类
 * @author: stop.yc
 * @create: 2023-08-09 20:15
 **/
@Data
public abstract class AbstractMessage {
    private Object data;

    private Integer messageContentType;

    private Long replyMessageId;
}
