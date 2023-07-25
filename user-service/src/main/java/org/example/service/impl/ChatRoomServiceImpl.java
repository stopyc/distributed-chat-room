package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChatRoomMapper;
import org.example.pojo.po.ChatRoom;
import org.example.service.IChatRoomService;
import org.springframework.stereotype.Service;

/**
 * @program: chat-room
 * @description: 聊天室业务层实现类
 * @author: stop.yc
 * @create: 2023-07-24 11:52
 **/
@Service
@Slf4j
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoom> implements IChatRoomService {

}
