package org.example.push;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageBOAdapter;
import org.example.adapter.MessageDTOAdapter;
import org.example.constant.MessageType;
import org.example.dao.MsgWriter;
import org.example.dao.UserDao;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.AtDTO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.vo.Text;
import org.example.websocket.GlobalWsMap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
        at(messageBO);
    }

    @Override
    public void push2Group(MessageBO messageBO) {
        MessageDTO messageDTO = MessageDTOAdapter.getGroupChatMsgDTO(messageBO, MessageType.CHAT_ROOM.getMessageType());
        //群聊消息进行存储
        msgWriter.saveGroupChatMsg(messageDTO);
        Set<Long> userIdSet = userDao.getUserIdSetByChatRoomId(messageDTO.getChatRoomId());
        GlobalWsMap.sendText(userIdSet, messageDTO, messageBO.getFromUserId());
        at(messageBO);
    }


    @Async
    protected void at(MessageBO messageBO) {
        if (!MessageType.isText(messageBO.getMessageContentType())) {
            return;
        }
        //如果有艾特消息，需要发送艾特
        Object data = messageBO.getData();
        Text text = JSONObject.parseObject(data.toString(), Text.class);
        if (CollectionUtils.isEmpty(text.getAtUserId())) {
            return;
        }
        AtDTO atDTO = MessageBOAdapter.getAtDTO((messageBO));
        for (Long userId : text.getAtUserId()) {
            msgWriter.putAtAck(messageBO.getChatRoomId(), userId, atDTO);
            if (!GlobalWsMap.isOnline(userId) || userId.equals(messageBO.getFromUserId())) {
                continue;
            }
            GlobalWsMap.sendText(userId, atDTO);
        }
    }
}
