package org.example.event_listener;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.config.WsMessageMqConfig;
import org.example.constant.RedisKey;
import org.example.event.PushWsMessageEvent;
import org.example.factory.MessageFactory;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageAck;
import org.example.pojo.vo.WsMessageVO;
import org.example.util.RedisNewUtil;
import org.example.websocket.GlobalWsMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: util
 * @description: 用户接受ws消息并推送消息到队列的监听器
 * @author: stop.yc
 * @create: 2023-07-18 09:46
 **/
@Component
@Slf4j
public class PushWsMessageListener {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ApplicationContext applicationContext;

    private PushWsMessageListener me() {
        return applicationContext.getBean(PushWsMessageListener.class);
    }

    @Async
    @EventListener(classes = PushWsMessageEvent.class)
    public void handleEvent(PushWsMessageEvent pushWsMessageEvent) {
        WsMessageVO wsMessageVO = pushWsMessageEvent.getWsMessageVO();
        //生成封装消息传输对象
        MessageBO messageBO = MessageFactory.generateMessageVo(wsMessageVO);
        MessageAck messageAck = BeanUtil.copyProperties(messageBO, MessageAck.class);
        ;
        try {
            //只要服务端一收到消息,必须立刻保证消息的可靠性,所以先将消息存入redis中
            RedisNewUtil.zput(RedisKey.MESSAGE_KEY, messageBO.getFromUserId(), messageBO, messageBO.getMessageId());
            messageAck.setAck(true);
            GlobalWsMap.sendText(messageBO.getFromUserId(), JSONObject.toJSONString(messageAck));
        } catch (Exception e) {
            messageAck.setAck(false);
            GlobalWsMap.sendText(messageBO.getFromUserId(), JSONObject.toJSONString(messageAck));
            log.error("消息保存到redis失败!发送消息nak!");
            return;
        }
        me().push2Mq(messageBO);
    }

    @Async
    public void push2Mq(MessageBO messageBO) {
        //封装消息确认报文
        MyMessageCorrelationData myMessageCorrelationData = BeanUtil.copyProperties(messageBO, MyMessageCorrelationData.class);
        //发送消息, ack和nak的逻辑在配置类中.
        rabbitTemplate.convertAndSend(WsMessageMqConfig.WS_EXCHANGE_NAME, "message.ws", JSONObject.toJSONString(messageBO), myMessageCorrelationData);
    }
}
