package org.example.pojo.vo;

/**
 * @program: chat-room
 * @description: 视频消息类
 * @author: stop.yc
 * @create: 2023-08-10 10:14
 **/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class Video {
    private String url;

    private String coverUrl;

    private Long duration;

    private Double length;

    private Double width;
}
