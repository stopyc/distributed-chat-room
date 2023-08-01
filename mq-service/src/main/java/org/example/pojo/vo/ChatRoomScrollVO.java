package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @program: chat-room
 * @description: 群聊消息VO
 * @author: stop.yc
 * @create: 2023-08-01 16:44
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ChatRoomScrollVO {

    @NotNull(message = "群聊id不能为空")
    private Long chatRoomId;

    @NotNull(message = "最大值不能为空")
    @Range(min = 0, message = "不能小于0")
    private Double max;

    @NotNull(message = "偏移量不能为空")
    @Range(min = 0, message = "不能小于0")
    private Long offset;
}
