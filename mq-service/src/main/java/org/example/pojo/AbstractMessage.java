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
    /**
     * 实际的数据体，文本、视频、音频、文件、图片
     */
    private Object data;

    /**
     * 消息的类型：文本0、视频1、音频2、文件3、图片4
     */
    private Integer messageContentType;


    /**
     * 回复的消息id
     */
    private Long replyMessageId;
}
