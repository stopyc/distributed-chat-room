package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisKey;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.dto.UserStatusDTO;
import org.example.service.IUserStatusService;
import org.example.util.RedisNewUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: chat-room
 * @description: 用户状态业务层实现类
 * @author: stop.yc
 * @create: 2023-08-02 12:02
 **/
@Service
@Slf4j
public class UserStatusServiceImpl implements IUserStatusService {
    @Override
    public ResultDTO getChatRoomUserStatus(Long chatRoomId) {
        Map<String, UserStatusDTO> mget = RedisNewUtil.mget(RedisKey.USER_ONLINE, "", UserStatusDTO.class);
        return ResultDTO.ok(mget.values());
    }

    @Override
    public ResultDTO getSingleChatUserStatus(Long toUserId) {
        return null;
    }
}
