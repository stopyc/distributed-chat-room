package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.feign.UserStatusClient;
import org.example.mapper.ChatRoomMapper;
import org.example.pojo.AtUserDTO;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.exception.FeignException;
import org.example.pojo.po.ChatRoom;
import org.example.service.IChatRoomService;
import org.example.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
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
    @Lazy
    private UserService userService;

    @Resource
    private UserStatusClient userStatusClient;


    @Override
    public ResultDTO listUsersInChatRoom(String chatroomId) {
        List<ChatRoom> list = lambdaQuery()
                .eq(ChatRoom::getChatRoomId, chatroomId)
                .list();
        List<Long> userIds = list.stream().map(ChatRoom::getUserId).collect(Collectors.toList());
        List<AtUserDTO> userList = userService.getUserListByIdList(userIds);
        ResultDTO chatRoomUserStatus;
        try {
            chatRoomUserStatus = userStatusClient.getChatRoomUserStatus(Long.parseLong(chatroomId));
            if (chatRoomUserStatus.getCode() != 200) {
                throw new FeignException(chatRoomUserStatus.getMsg());
            }
        } catch (Exception e) {
            throw new FeignException(e.getMessage());
        }
        Object data = chatRoomUserStatus.getData();
        if (data != null) {
            // 获取在线用户列表
            List userStatusList = (ArrayList) data;
            Map<Long, Integer> map = new HashMap<>(userStatusList.size());
            userStatusList.forEach(userStatus -> {
                AtUserDTO atUserDTO = BeanUtil.mapToBean((LinkedHashMap) userStatus, AtUserDTO.class, true);
                map.put(atUserDTO.getUserId(), 1);
            });
            if (!CollectionUtils.isEmpty(map)) {
                userList.forEach(user -> {
                    if (map.containsKey(user.getUserId())) {
                        user.setOnline(true);
                    }
                });
            }
        }

        return ResultDTO.ok(userList);
    }
}
