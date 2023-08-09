package org.example.wsjson;

import lombok.Data;

/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2023-08-09 16:09
 **/
@Data
public class VO {
    private Integer messageType;

    private Object data;
}
