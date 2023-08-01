package org.example.push;

import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageDTOAdapter;
import org.example.constant.MessageType;
import org.example.dao.MsgWriter;
import org.example.dao.UserDao;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.websocket.GlobalWsMap;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Resource
    private UserDao userDao;

    @Resource
    private MsgWriter msgWriter;

    @Override
    public void push2User(MessageBO messageBO) {
        MessageDTO messageDTO = MessageDTOAdapter.getSingleChatMsgDTO(messageBO, MessageType.SINGLE_CHAT.getMessageType());
        //单聊的消息进行存储。
        msgWriter.saveSingleChatMsg(messageDTO);
        GlobalWsMap.sendText(messageBO.getToUserId(), messageDTO);
    }

    @Override
    public void push2Group(MessageBO messageBO) {
        MessageDTO messageDTO = MessageDTOAdapter.getGroupChatMsgDTO(messageBO, MessageType.CHAT_ROOM.getMessageType());
        //群聊消息进行存储
        msgWriter.saveGroupChatMsg(messageDTO);
        Set<Long> userIdSet = userDao.getUserIdSetByChatRoomId(messageDTO.getChatRoomId());
        GlobalWsMap.sendText(userIdSet, messageDTO, messageBO.getFromUserId());
    }
}
