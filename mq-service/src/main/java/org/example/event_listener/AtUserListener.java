package org.example.event_listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.adapter.MessageDTOAdapter;
import org.example.constant.MessageType;
import org.example.dao.MsgWriter;
import org.example.event.AtUserEvent;
import org.example.pojo.dto.AtDTO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.vo.Text;
import org.example.websocket.GlobalWsMap;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * @program: chatroom
 * @description: 艾特用户事件监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class AtUserListener {

    @Resource
    private MsgWriter msgWriter;

    @Async
    @EventListener(classes = AtUserEvent.class)
    public void handleEvent(AtUserEvent atUserEvent) {
        MessageDTO messageDTO = atUserEvent.getMessageDTO();
        if (!MessageType.isText(messageDTO.getMessageContentType())) {
            return;
        }
        //如果有艾特消息，需要发送艾特
        Object data = messageDTO.getData();
        Text text = JSONObject.parseObject(data.toString(), Text.class);
        if (CollectionUtils.isEmpty(text.getAtUserId())) {
            return;
        }
        AtDTO atDTO = MessageDTOAdapter.getAtDTO((messageDTO));
        for (Long userId : text.getAtUserId()) {
            msgWriter.putAtAck(messageDTO.getChatRoomId(), userId, atDTO);
            if (!GlobalWsMap.isOnline(userId) || userId.equals(messageDTO.getFromUserId())) {
                continue;
            }
            GlobalWsMap.sendText(userId, atDTO);
        }
    }
}
