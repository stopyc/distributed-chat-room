package org.example.utils;

import cn.hutool.core.bean.BeanUtil;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageAck;

/**
 * @program: chat-room
 * @description: 消息ack工具类
 * @author: stop.yc
 * @create: 2023-07-20 15:18
 **/
public class MessageAckUtil {
    public static MessageAck getMessageAck(MessageBO messageBO) {
        MessageAck mack = BeanUtil.copyProperties(messageBO, MessageAck.class);
        mack.setAck(true);
        mack.setMessageType(0);
        return mack;
    }

    public static MessageAck getBusinessMessageAck(MessageBO messageBO) {
        MessageAck mack = BeanUtil.copyProperties(messageBO, MessageAck.class);
        mack.setAck(true);
        mack.setMessageType(1);
        return mack;
    }

    public static MessageAck getMessageNak(MessageBO messageBO) {
        MessageAck mack = BeanUtil.copyProperties(messageBO, MessageAck.class);
        mack.setAck(false);
        mack.setMessageType(0);
        return mack;
    }

    public static MessageAck getBusinessMessageNak(MessageBO messageBO) {
        MessageAck mack = BeanUtil.copyProperties(messageBO, MessageAck.class);
        mack.setAck(false);
        mack.setMessageType(1);
        return mack;
    }
}
