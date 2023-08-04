package org.example.dao;

import org.example.constant.RedisKey;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.vo.WsMessageVO;
import org.example.util.RedisNewUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @program: chat-room
 * @description: 消息存储服务
 * @author: stop.yc
 * @create: 2023-07-31 16:25
 **/
@Component
public class MsgWriter {
    @Resource
    private MsgReader msgReader;

    public void saveSingleChatMsg(MessageDTO messageDTO) {
        Long fromUserId = messageDTO.getFromUserId();
        Long toUserId = messageDTO.getToUserId();
        //小的在前面，大的在后面
        String key = fromUserId > toUserId
                ? toUserId + ":" + fromUserId
                : fromUserId + ":" + toUserId;
        //单聊的消息进行存储。
        //TODO;异步存储，避免阻塞主线程，调用代理层，通过队列写数据库
        if (msgReader.hasMsg(RedisKey.SINGLE_CHAT, key, messageDTO.getMessageId(), MessageDTO.class)) {
            return;
        }
        RedisNewUtil.zput(RedisKey.SINGLE_CHAT, key, messageDTO, messageDTO.getMessageId());
    }

    public void saveGroupChatMsg(MessageDTO messageDTO) {
        if (msgReader.hasMsg(RedisKey.GROUP_CHAT, messageDTO.getChatRoomId(), messageDTO.getMessageId(), MessageDTO.class)) {
            return;
        }
        //群聊消息进行存储
        RedisNewUtil.zput(RedisKey.GROUP_CHAT, messageDTO.getChatRoomId(), messageDTO, messageDTO.getMessageId());
    }


    public void saveDurableMsg(WsMessageVO wsMessageVO) {
        try {
            WsMessageVO clone = wsMessageVO.clone();
            clone.setMyWebSocket(null);
            RedisNewUtil.zput(RedisKey.MESSAGE_KEY, clone.getFromUserId(), clone, clone.getClientMessageId());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDurableMsg(MessageBO messageBO) {
        RedisNewUtil.zput(RedisKey.OK_MESSAGE_KEY, messageBO.getFromUserId(), messageBO, messageBO.getClientMessageId());
    }

    public void saveAckMsg(MessageBO messageBO) {
        //Redis的Ack队列
        RedisNewUtil.put(RedisKey.ACK_MESSAGE_KEY,
                messageBO.getFromUserId() + ":" + messageBO.getClientMessageId(),
                messageBO,
                RedisKey.ACK_EXPIRATION_TIME,
                TimeUnit.MILLISECONDS);
    }
}
