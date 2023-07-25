package org.example.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: chat-room
 * @description: 聊天室PO
 * @author: stop.yc
 * @create: 2023-07-24 11:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ChatRoomBO {
    private Long chatRoomId;

    private Long userId;
}
