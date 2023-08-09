package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @program: chat-room
 * @description: 文本消息类
 * @author: stop.yc
 * @create: 2023-08-09 20:20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class Text {
    private List<Long> atUserId;

    private String content;
}
