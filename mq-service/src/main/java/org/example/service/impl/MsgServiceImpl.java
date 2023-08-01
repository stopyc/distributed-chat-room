package org.example.service.impl;

import org.example.constant.RedisKey;
import org.example.dao.MsgReader;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.dto.ScrollingPaginationDTO;
import org.example.pojo.vo.ScrollingPaginationVO;
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
    public ScrollingPaginationDTO<MessageDTO> getMsg(ScrollingPaginationVO scrollingPaginationVO) {
        String prefixKey = null;
        String msgKey = null;
        //群聊
        if (scrollingPaginationVO.getMessageType() == 6) {
            prefixKey = RedisKey.GROUP_CHAT;
            msgKey = scrollingPaginationVO.getChatRoomId().toString();
        } else if (scrollingPaginationVO.getMessageType() == 7) {
            prefixKey = RedisKey.SINGLE_CHAT;
            msgKey = (scrollingPaginationVO.getFromUserId() > scrollingPaginationVO.getToUserId()
                    ? scrollingPaginationVO.getToUserId() + ":" + scrollingPaginationVO.getFromUserId()
                    : scrollingPaginationVO.getFromUserId() + ":" + scrollingPaginationVO.getToUserId());
        }
        ScrollingPaginationDTO<MessageDTO> msg = msgReader.getMsg(prefixKey, msgKey, scrollingPaginationVO.getMax(), scrollingPaginationVO.getOffset(), MessageDTO.class);
        return msg;
    }
}
