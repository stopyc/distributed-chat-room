package org.example.controller;

import org.example.pojo.dto.ResultDTO;
import org.example.pojo.po.ChatRoom;
import org.example.service.IChatRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: chat-room
 * @description: 聊天室控制层
 * @author: stop.yc
 * @create: 2023-07-24 11:45
 **/
@RestController
@RequestMapping(value = "/chatRoom", produces = "application/json;charset=utf-8")
public class ChatRoomController {
    @Resource
    private IChatRoomService chatRoomService;

    @GetMapping("/inner/{chatRoomId}")
    //@PreAuthorize("@ss.hasPermi('system:admin:query')")
    public ResultDTO getUserSetByChatRoomId(@PathVariable("chatRoomId") Long chatRoomId) {
        List<Long> list = chatRoomService.lambdaQuery()
                .eq(ChatRoom::getChatRoomId, chatRoomId)
                .list()
                .stream()
                .map(ChatRoom::getUserId)
                .collect(Collectors.toList());

        return ResultDTO.ok(new HashSet<>(list));
    }
}
