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
import org.example.utils.MessageAckUtil;
import org.example.websocket.GlobalWsMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        //服务器收到消息,先判断这个消息是否之前已经收到了
        WsMessageVO wsMessageVO = pushWsMessageEvent.getWsMessageVO();
        MessageBO redisMbo = RedisNewUtil.get(RedisKey.ACK_MESSAGE_KEY,
                wsMessageVO.getFromUserId() + ":" + wsMessageVO.getClientMessageId(),
                MessageBO.class);
        if (!Objects.isNull(redisMbo)) {
            log.info("客户端消息为 {} 的消息已经收到过了,直接返回ack!", redisMbo.getClientMessageId());
            //直接返回ack
            MessageAck messageAck = MessageAckUtil.getMessageAck(redisMbo);
            GlobalWsMap.sendText(redisMbo.getFromUserId(), JSONObject.toJSONString(messageAck));
            return;
        }
        //生成封装消息传输对象
        MessageBO messageBO = MessageFactory.generateMessageVo(wsMessageVO);
        try {
            //只要服务端一收到消息,必须立刻保证消息的可靠性,所以先将消息存入redis中
            //Redis消息队列
            RedisNewUtil.zput(RedisKey.MESSAGE_KEY, messageBO.getFromUserId(), messageBO, messageBO.getClientMessageId());
            //Redis的Ack队列
            RedisNewUtil.put(RedisKey.ACK_MESSAGE_KEY,
                    messageBO.getFromUserId() + ":" + messageBO.getClientMessageId(),
                    messageBO,
                    24L,
                    TimeUnit.HOURS);
            MessageAck messageAck = MessageAckUtil.getMessageAck(messageBO);
            GlobalWsMap.sendText(messageBO.getFromUserId(), JSONObject.toJSONString(messageAck));
        } catch (Exception e) {
            MessageAck messageAck = MessageAckUtil.getMessageNak(messageBO);
            GlobalWsMap.sendText(messageBO.getFromUserId(), JSONObject.toJSONString(messageAck));
            log.error("消息保存到redis失败!发送消息nak!");
        }
        //me().push2Mq(messageBO);
    }

    @Async
    public void push2Mq(MessageBO messageBO) {
        //封装消息确认报文
        MyMessageCorrelationData myMessageCorrelationData = BeanUtil.copyProperties(messageBO, MyMessageCorrelationData.class);
        //发送消息, ack和nak的逻辑在配置类中.
        rabbitTemplate.convertAndSend(WsMessageMqConfig.WS_EXCHANGE_NAME, "message.ws", JSONObject.toJSONString(messageBO), myMessageCorrelationData);
    }
}
