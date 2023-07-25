package org.example.event_listener;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.config.WsMessageMqConfig;
import org.example.constant.RedisConstant;
import org.example.constant.RedisKey;
import org.example.event.PushWsMessageEvent;
import org.example.factory.MessageFactory;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.dto.MessageDTO;
import org.example.pojo.exception.SystemException;
import org.example.pojo.vo.WsMessageVO;
import org.example.util.RedisNewUtil;
import org.example.utils.MessageAckUtil;
import org.example.websocket.GlobalWsMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;
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

        Set<MessageBO> durableMsg = RedisNewUtil.zget(RedisKey.MESSAGE_KEY, wsMessageVO.getFromUserId(), wsMessageVO.getClientMessageId(), MessageBO.class);
        if (!CollectionUtils.isEmpty(durableMsg)) {
            //持久队列该消息不为空,表示之前收到过了,但是可能ack消息丢失了,或者ack消息还没来得及发送,或者最终的业务ack消息发送失败了
            MessageBO redisMbo = RedisNewUtil.get(RedisKey.ACK_MESSAGE_KEY,
                    wsMessageVO.getFromUserId() + ":" + wsMessageVO.getClientMessageId(),
                    MessageBO.class);
            if (!Objects.isNull(redisMbo)) {
                //持久化队列里面有,ack队列里面也有,等待ack队列操作即可
                log.info("客户端消息为 {} 的消息已经收到过了,直接返回ack!", redisMbo.getClientMessageId());
            } else {
                //Redis的Ack队列
                if (durableMsg.size() > 1) {
                    throw new SystemException("redis中的消息队列中的客户端消息id 为 " + wsMessageVO.getClientMessageId() + " 不止一条,请检查!");
                }
                for (MessageBO messageBO : durableMsg) {
                    RedisNewUtil.put(RedisKey.ACK_MESSAGE_KEY,
                            messageBO.getFromUserId() + ":" + messageBO.getClientMessageId(),
                            messageBO,
                            RedisConstant.ACK_EXPIRATION_TIME,
                            TimeUnit.SECONDS);
                    redisMbo = messageBO;
                    me().push2Mq(messageBO);
                }
            }
            //直接返回ack
            MessageDTO messageAck = MessageAckUtil.getMessageAck(redisMbo);
            GlobalWsMap.sendText(redisMbo.getFromUserId(), messageAck);
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
                    RedisConstant.ACK_EXPIRATION_TIME,
                    TimeUnit.SECONDS);
            MessageDTO messageAck = MessageAckUtil.getMessageAck(messageBO);
            GlobalWsMap.sendText(messageBO.getFromUserId(), messageAck);
            me().push2Mq(messageBO);
        } catch (Exception e) {
            MessageDTO messageAck = MessageAckUtil.getMessageNak(messageBO);
            GlobalWsMap.sendText(messageBO.getFromUserId(), messageAck);
            log.error("消息保存到redis失败!发送消息nak!");
        }
    }

    @Async
    public void push2Mq(MessageBO messageBO) {
        //封装消息确认报文
        MyMessageCorrelationData myMessageCorrelationData = BeanUtil.copyProperties(messageBO, MyMessageCorrelationData.class);
        //发送消息, ack和nak的逻辑在配置类中.
        rabbitTemplate.convertAndSend(WsMessageMqConfig.WS_EXCHANGE_NAME, "message.ws", JSONObject.toJSONString(messageBO), myMessageCorrelationData);
    }
}