package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.po.ChatRoom;

/**
 * @program: chat-room
 * @description: 聊天室持久层
 * @author: stop.yc
 * @create: 2023-07-24 11:53
 **/
@Mapper
public interface ChatRoomMapper extends BaseMapper<ChatRoom> {
}

