package org.example.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.example.feign.UserClient;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.vo.ResultVO;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @program: chat-room
 * @description: obj转dto
 * @author: stop.yc
 * @create: 2023-07-24 11:05
 **/
@Component
public class MessageDTOAdapter {

    private static UserClient userClient;

    static {
        MessageDTOAdapter.userClient = SpringUtil.getBean(UserClient.class);
    }

    public static MessageDTO getMessageDTO(MessageBO messageBO, @NotNull Integer messageType) {
        ResultDTO fromUserResult = userClient.getById(messageBO.getFromUserId());
        ResultDTO toUserResult = userClient.getById(messageBO.getToUserId());
        MessageDTO dto = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        if (fromUserResult.getCode() == 200) {
            UserBO userBO = BeanUtil.mapToBean((LinkedHashMap) fromUserResult.getData(), UserBO.class, true);
            dto.setFromUserName(userBO.getUsername());
        }

        if (toUserResult.getCode() == 200) {
            UserBO userBO = BeanUtil.mapToBean((LinkedHashMap) toUserResult.getData(), UserBO.class, true);
            dto.setToUserName(userBO.getUsername());
        }

        dto.setMessageType(messageType);
        return dto;
    }

    public static Set<Long> getUserIdSetByChatRoomId(Long chatRoomId) {
        ResultVO userSetByChatRoomId = userClient.getUserSetByChatRoomId(chatRoomId);
        HashSet<Long> hashset = new HashSet<>();
        if (userSetByChatRoomId.getCode() == 200) {
            List data = (ArrayList) userSetByChatRoomId.getData();
            for (Object datum : data) {
                hashset.add((long) ((int) datum));
            }
            return hashset;
        }

        return Collections.emptySet();
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
        mack.setMessageType(0);
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getBusinessMessageAck(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(true);
        mack.setMessageType(1);
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getMessageNak(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(false);
        mack.setMessageType(0);
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getBusinessMessageNak(MessageBO messageBO) {
        MessageDTO mack = BeanUtil.copyProperties(messageBO, MessageDTO.class);
        mack.setAck(false);
        mack.setMessageType(1);
        mack.setMessage(null);
        mack.setByteArray(null);
        return mack;
    }

    public static MessageDTO getBeatPong(WsMessageVO wsMessageVO) {
        MessageDTO pong = BeanUtil.copyProperties(wsMessageVO, MessageDTO.class);
        pong.setAck(true);
        pong.setMessageType(4);
        pong.setMessage("pong");
        pong.setByteArray(null);
        return pong;
    }
}
