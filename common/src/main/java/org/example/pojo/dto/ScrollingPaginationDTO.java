package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

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
public class ScrollingPaginationDTO<T> {
    private Long max;

    private Long offset;

    private List<T> resultList;
}
