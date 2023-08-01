package org.example.controller;

import org.example.pojo.dto.ResultDTO;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.vo.ScrollingPaginationVO;
import org.example.service.IMsgService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

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

    @PostMapping("/public/getMsg")
    public ResultDTO getMsg(@RequestBody ScrollingPaginationVO scrollingPaginationVO) {
        if (Objects.isNull(scrollingPaginationVO)) {
            throw new BusinessException("参数不能为空");
        }
        scrollingPaginationVO.validate();
        return ResultDTO.ok(msgService.getMsg(scrollingPaginationVO));
    }
}
