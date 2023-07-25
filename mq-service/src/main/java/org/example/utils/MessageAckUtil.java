package org.example.utils;

import cn.hutool.core.bean.BeanUtil;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;

/**
 * @program: chat-room
 * @description: 消息ack工具类
 * @author: stop.yc
 * @create: 2023-07-20 15:18
 **/
public class MessageAckUtil {
    public static MessageDTO getMessageAck(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(true);
        mack.setMessageType(0);
        return mack;
    }

    public static MessageDTO getBusinessMessageAck(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(true);
        mack.setMessageType(1);
        return mack;
    }

    public static MessageDTO getMessageNak(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(false);
        mack.setMessageType(0);
        return mack;
    }

    public static MessageDTO getBusinessMessageNak(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(false);
        mack.setMessageType(1);
        return mack;
    }
}
