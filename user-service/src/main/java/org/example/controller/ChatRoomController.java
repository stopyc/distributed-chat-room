package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.pojo.dto.ResultDTO;
import org.example.service.IChatRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: chat-room
 * @description: 聊天室控制层
 * @author: stop.yc
 * @create: 2023-07-24 11:45
 **/
@RestController
@RequestMapping(value = "/chatRoom", produces = "application/json;charset=utf-8")
@RequiredArgsConstructor
public class ChatRoomController {
    private final IChatRoomService chatRoomService;

    @GetMapping("/inner/{chatRoomId}")
    //@PreAuthorize("@ss.hasPermi('system:admin:query')")
    public ResultDTO getUserSetByChatRoomId(@PathVariable("chatRoomId") Long chatRoomId) {
        List<Long> list = chatRoomService.getUserSetByChatRoomId(chatRoomId);
        return ResultDTO.ok(list);
    }

    @GetMapping("/list/{chatroomId}")
    public ResultDTO listUsersInChatRoom(@PathVariable String chatroomId) {
        return chatRoomService.listUsersInChatRoom(chatroomId);
    }
}
