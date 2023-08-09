package org.example.controller;

import org.example.pojo.dto.ResultDTO;
import org.example.pojo.vo.ChatRoomScrollVO;
import org.example.pojo.vo.SingleScrollVO;
import org.example.service.IMsgService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @program: chat-room
 * @description: 消息控制层
 * @author: stop.yc
 * @create: 2023-07-31 17:07
 **/

@RestController
@RequestMapping(value = "/msg", produces = "application/json;charset=utf-8")
public class MsgController {

    @Resource
    private IMsgService msgService;

    @PostMapping("/getMsg")
    public ResultDTO getMsg(@RequestBody @Validated ChatRoomScrollVO chatRoomScrollVO) {
        return ResultDTO.ok(msgService.getChatRoomMsg(chatRoomScrollVO));
    }

    @PostMapping("/getSingleMsg")
    public ResultDTO getSingleMsg(@RequestBody @Validated SingleScrollVO singleScrollVO) {
        return ResultDTO.ok(msgService.getSingleMsg(singleScrollVO));
    }

    @DeleteMapping("/{chatroomId}/{messageId}")
    public ResultDTO ackAtMsg(@PathVariable("chatroomId") String chatroomId,
                              @PathVariable("messageId") String messageId) {
        msgService.ackAtMsg(chatroomId, messageId);
        return ResultDTO.ok();
    }

    @GetMapping("/{chatroomId}")
    public ResultDTO getAtMsg(@PathVariable("chatroomId") String chatroomId) {
        return ResultDTO.ok(msgService.getAtMsg(chatroomId));
    }
}
