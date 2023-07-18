package org.example.pojo.vo.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.pojo.vo.MessageVO;

import java.io.Serializable;

/**
 * @program: chat-room
 * @description: 群聊消息
 * @author: stop.yc
 * @create: 2023-07-18 16:39
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatMessage extends MessageVO implements Serializable {
    /**
     * 群聊id
     */
    private Long chatRoomId;
}
