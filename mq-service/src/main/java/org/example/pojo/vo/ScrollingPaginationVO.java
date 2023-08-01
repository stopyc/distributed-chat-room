package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: chat-room
 * @description: 滚动分页DTO
 * @author: stop.yc
 * @create: 2023-07-31 16:55
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ScrollingPaginationVO {

    private Integer messageType;

    private Long fromUserId;

    private Long toUserId;

    private Long chatRoomId;

    private Double max;

    private Long offset;

    public void validate() {
        if (max == null || offset == null || max < 0 || offset < 0) {
            throw new IllegalArgumentException("滚动分页VO参数错误");
        }
    }
}
