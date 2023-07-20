package org.example.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.exception.SystemException;
import org.example.pojo.vo.WsMessageVO;
import org.example.utils.IdUtil;
import org.springframework.stereotype.Component;

/**
 * @program: chat-room
 * @description: 消息Vo生产工厂
 * @author: stop.yc
 * @create: 2023-07-19 09:47
 **/
@Component
@Slf4j
public class MessageFactory {

    private static IdUtil idUtil;

    static {
        MessageFactory.idUtil = SpringUtil.getBean(IdUtil.class);
    }

    private MessageFactory() {

    }

    /**
     * 生成消息vo
     *
     * @param wsMessageVO: wsMessageVo,也就是ws消息的原始vo
     * @return: org.example.pojo.vo.MessageVO
     */
    public static MessageBO generateMessageVo(WsMessageVO wsMessageVO) {
        MessageBO messageBO = BeanUtil.copyProperties(wsMessageVO, MessageBO.class);
        messageBO.setServerTime(System.currentTimeMillis());
        long begin = System.currentTimeMillis();
        long snowflakeId;
        try {
            snowflakeId = idUtil.nextId();
        } catch (Exception e) {
            throw new SystemException(e.getMessage());
        }
        begin = System.currentTimeMillis() - begin;
        log.debug("begin 为: {}", begin);
        messageBO.setMessageId(snowflakeId);
        if (messageBO.getIsText()) {
            messageBO.setMessage(messageBO.getMessage());
        } else {
            messageBO.setByteArray(messageBO.getByteArray());
        }
        return messageBO;
    }
}
