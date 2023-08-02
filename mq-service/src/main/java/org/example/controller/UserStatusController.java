package org.example.controller;

import org.example.pojo.dto.ResultDTO;
import org.example.service.IUserStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: chat-room
 * @description: 用户状态控制层
 * @author: stop.yc
 * @create: 2023-08-02 11:53
 **/
@RestController
@RequestMapping(value = "/status", produces = "application/json;charset=utf-8")
public class UserStatusController {

    @Resource
    private IUserStatusService userStatusService;

    @GetMapping("/getChatRoomUserStatus/{chatRoomId}")
    public ResultDTO getChatRoomUserStatus(@PathVariable("chatRoomId") Long chatRoomId) {
        return userStatusService.getChatRoomUserStatus(chatRoomId);
    }

    @GetMapping("/getSingleChatUserStatus/{toUserId}")
    public ResultDTO getSingleChatUserStatus(@PathVariable("toUserId") Long toUserId) {
        return userStatusService.getSingleChatUserStatus(toUserId);
    }
}
