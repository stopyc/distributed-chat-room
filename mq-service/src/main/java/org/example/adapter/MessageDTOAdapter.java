package org.example.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.MessageType;
import org.example.dao.UserDao;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.AtDTO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @program: chat-room
 * @description: obj转dto
 * @author: stop.yc
 * @create: 2023-07-24 11:05
 **/
@Component
@Slf4j
public class MessageDTOAdapter {

    private static UserDao userDao;

    static {
        MessageDTOAdapter.userDao = SpringUtil.getBean(UserDao.class);
    }

    public static MessageDTO getGroupChatMsgDTO(MessageBO messageBO, @NotNull Integer messageType) {
        MessageDTO dto = getFromUserDTO(messageBO);
        dto.setMessageType(messageType);
        return dto;
    }

    private static MessageDTO getFromUserDTO(MessageBO messageBO) {
        MessageDTO dto = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        UserBO fromUserBo = userDao.getUserBoByUserId(messageBO.getFromUserId());
        dto.setFromUserName(Optional.ofNullable(fromUserBo).map(UserBO::getUsername).orElse("未知用户"));
        dto.setColor(Optional.ofNullable(fromUserBo).map(UserBO::getColor).orElse("rgb(255,255,255)"));
        dto.setIcon(Optional.ofNullable(fromUserBo).map(UserBO::getIcon).orElse("icon-dad"));
        return dto;
    }

    public static MessageDTO getSingleChatMsgDTO(MessageBO messageBO, @NotNull Integer messageType) {
        MessageDTO dto = getFromUserDTO(messageBO);
        UserBO toUserBo = userDao.getUserBoByUserId(messageBO.getToUserId());
        dto.setToUserName(Optional.ofNullable(toUserBo).map(UserBO::getUsername).orElse("未知用户"));
        dto.setMessageType(messageType);
        return dto;
    }

    public static MessageDTO getMessageDTO(String msg, @NotNull Integer messageType) {
        MessageDTO dto = new MessageDTO();
        dto.setMessage(msg);
        dto.setMessageType(messageType);
        return dto;
    }

    public static MessageDTO getMessageAck(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(true);
        mack.setMessageType(MessageType.OFFSET_ACK.getMessageType());
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getBusinessMessageAck(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(true);
        mack.setMessageType(MessageType.BUSINESS_ACK.getMessageType());
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getMessageNak(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(false);
        mack.setMessageType(MessageType.OFFSET_ACK.getMessageType());
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getBusinessMessageNak(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(false);
        mack.setMessageType(MessageType.BUSINESS_ACK.getMessageType());
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getBeatPong(WsMessageVO wsMessageVO) {
        MessageDTO pong = BeanUtil.copyProperties(wsMessageVO, MessageDTO.class);
        pong.setAck(true);
        pong.setMessageType(MessageType.HEARTBEAT.getMessageType());
        pong.setMessage("pong");
        pong.setByteArray(null);
        return pong;
    }

    public static AtDTO getAtDTO(MessageDTO messageDTO) {
        AtDTO atDTO = BeanUtil.copyProperties(messageDTO, AtDTO.class);
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
