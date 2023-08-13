package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChatRoomMapper;
import org.example.pojo.AtUserDTO;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.po.ChatRoom;
import org.example.service.IChatRoomService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: chat-room
 * @description: 聊天室业务层实现类
 * @author: stop.yc
 * @create: 2023-07-24 11:52
 **/
@Service
@Slf4j
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoom> implements IChatRoomService {


    @Resource
    private UserService userService;

    @Override
    public ResultDTO listUsersInChatRoom(String chatroomId) {
        List<ChatRoom> list = lambdaQuery()
                .eq(ChatRoom::getChatRoomId, chatroomId)
                .list();
        List<Long> userIds = list.stream().map(ChatRoom::getUserId).collect(Collectors.toList());
        List<AtUserDTO> userList = userService.getUserListByIdList(userIds);
        return ResultDTO.ok(userList);
    }
}
