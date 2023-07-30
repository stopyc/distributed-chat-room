package org.example.push;

import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageDTOAdapter;
import org.example.constant.RedisKey;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.util.RedisNewUtil;
import org.example.websocket.GlobalWsMap;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @program: chat-room
 * @description: 推送代理层
 * @author: stop.yc
 * @create: 2023-07-30 17:14
 **/
@Component
@Slf4j
public class PushProxy implements PushWorker {

    @Override
    public void push2User(MessageBO messageBO) {
        MessageDTO messageDTO = MessageDTOAdapter.getMessageDTO(messageBO, 7);
        Long fromUserId = messageDTO.getFromUserId();
        Long toUserId = messageDTO.getToUserId();
        //小的在前面，大的在后面
        String key = fromUserId > toUserId
                ? toUserId + ":" + fromUserId
                : fromUserId + ":" + toUserId;
        //单聊的消息进行存储。
        //TODO;异步存储，避免阻塞主线程
        RedisNewUtil.zput(RedisKey.SINGLE_CHAT, key, messageDTO, messageDTO.getMessageId());
        GlobalWsMap.sendText(messageBO.getToUserId(), messageDTO);
    }

    @Override
    public void push2Group(MessageBO messageBO) {
        MessageDTO messageDTO = MessageDTOAdapter.getMessageDTO(messageBO, 6);
        RedisNewUtil.zput(RedisKey.GROUP_CHAT, messageDTO.getChatRoomId(), messageDTO, messageDTO.getMessageId());
        Set<Long> userIdSet = MessageDTOAdapter.getUserIdSetByChatRoomId(messageDTO.getChatRoomId());
        GlobalWsMap.sendText(userIdSet, messageDTO, messageBO.getFromUserId());
    }
}
