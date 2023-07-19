package org.example.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.event.AcceptMessageEvent;
import org.example.factory.MessageFactory;
import org.example.pojo.vo.MessageVO;
import org.example.pojo.vo.WsMessageVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: util
 * @description: 用户接受消息监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class AcceptMessageListener {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Async
    @EventListener(classes = AcceptMessageEvent.class)
    public void handleEvent(AcceptMessageEvent acceptMessageEvent) {
        WsMessageVO wsMessageVO = acceptMessageEvent.getWsMessageVO();
        MessageVO messageVO = MessageFactory.generateMessageVo(wsMessageVO);
        rabbitTemplate.convertAndSend("ws_fanout_exchange", "message.ws", JSONObject.toJSONString(messageVO));
    }
}
