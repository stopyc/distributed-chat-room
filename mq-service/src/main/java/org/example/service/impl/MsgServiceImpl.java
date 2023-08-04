package org.example.service.impl;

import org.example.constant.RedisKey;
import org.example.dao.MsgReader;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.dto.ScrollingPaginationDTO;
import org.example.pojo.vo.ChatRoomScrollVO;
import org.example.pojo.vo.SingleScrollVO;
import org.example.service.IMsgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: chat-room
 * @description: 消息业务处理实现层
 * @author: stop.yc
 * @create: 2023-07-31 17:13
 **/
@Service
public class MsgServiceImpl implements IMsgService {

    @Resource
    private MsgReader msgReader;

    @Override
    public ScrollingPaginationDTO<MessageDTO> getChatRoomMsg(ChatRoomScrollVO chatRoomScrollVO) {
        String prefixKey;
        String msgKey;
        //群聊
        prefixKey = RedisKey.GROUP_CHAT;
        msgKey = chatRoomScrollVO.getChatRoomId().toString();
        return msgReader.getMsg(prefixKey, msgKey, chatRoomScrollVO.getMax(), chatRoomScrollVO.getOffset(), 20, MessageDTO.class);
    }

    @Override
    public ScrollingPaginationDTO<MessageDTO> getSingleMsg(SingleScrollVO singleScrollVO) {
        String prefixKey;
        String msgKey;
        prefixKey = RedisKey.SINGLE_CHAT;
        msgKey = (singleScrollVO.getFromUserId() > singleScrollVO.getToUserId()
                ? singleScrollVO.getToUserId() + ":" + singleScrollVO.getFromUserId()
                : singleScrollVO.getFromUserId() + ":" + singleScrollVO.getToUserId());
        return msgReader.getMsg(prefixKey, msgKey, singleScrollVO.getMax(), singleScrollVO.getOffset(), 20, MessageDTO.class);
    }
}
