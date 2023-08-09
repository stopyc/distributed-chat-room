package org.example.adapter;

import cn.hutool.core.bean.BeanUtil;
import org.example.constant.MessageType;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.AtDTO;

/**
 * @program: chat-room
 * @description: messageBo适配器
 * @author: stop.yc
 * @create: 2023-08-09 22:24
 **/
public class MessageBOAdapter {

    public static AtDTO getAtDTO(MessageBO messageBO) {
        AtDTO atDTO = BeanUtil.copyProperties(messageBO, AtDTO.class);
        atDTO.setClientTime(null);
        atDTO.setMessage(null);
        atDTO.setByteArray(null);
        atDTO.setIsText(false);
        atDTO.setToUserId(null);
        atDTO.setToUserName(null);
        atDTO.setAck(null);
        atDTO.setColor(null);
        atDTO.setIcon(null);
        atDTO.setData(null);
        atDTO.setMessageContentType(null);
        atDTO.setReplyMessageId(null);
        atDTO.setMessageType(MessageType.AT.getMessageType());
        atDTO.setMessageContentType(MessageType.AT.getMessageType());
        return atDTO;
    }
}
