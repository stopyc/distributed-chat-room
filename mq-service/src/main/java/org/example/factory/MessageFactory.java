package org.example.factory;

import cn.hutool.core.bean.BeanUtil;
import org.example.pojo.vo.MessageVO;
import org.example.pojo.vo.WsMessageVO;

/**
 * @program: chat-room
 * @description: 消息Vo生产工厂
 * @author: stop.yc
 * @create: 2023-07-19 09:47
 **/
public class MessageFactory {

    private MessageFactory() {

    }

    /**
     * 生成消息vo
     *
     * @param wsMessageVO: wsMessageVo,也就是ws消息的原始vo
     * @return: org.example.pojo.vo.MessageVO
     */
    public static MessageVO generateMessageVo(WsMessageVO wsMessageVO) {
        MessageVO messageVO = BeanUtil.copyProperties(wsMessageVO, MessageVO.class);
        messageVO.setServerTime(System.currentTimeMillis());
        //TODO:异步回调生成id
        messageVO.setMessageId(1L);
        if (messageVO.getIsText()) {
            messageVO.setMessage(messageVO.getMessage());
        } else {
            messageVO.setByteArray(messageVO.getByteArray());
        }
        return messageVO;
    }
}
