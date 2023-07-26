package org.example.event;

import cn.hutool.core.bean.BeanUtil;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.context.ApplicationEvent;

/**
 * @author YC104
 */
public class PushBusinessAckEvent extends ApplicationEvent {

    private final MessageBO messageBO;

    private WsMessageVO wsMessageVO;

    public PushBusinessAckEvent(Object source, WsMessageVO wsMessageVO) {
        super(source);
        this.wsMessageVO = wsMessageVO;
        this.messageBO = BeanUtil.copyProperties(wsMessageVO, MessageBO.class);
    }

    public PushBusinessAckEvent(Object source, MessageBO messageBO) {
        super(source);
        this.messageBO = messageBO;
    }

    public MessageBO getMessageBO() {
        return messageBO;
    }
}
