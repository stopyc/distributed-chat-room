package org.example.mq.push;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import org.example.config.WsMessageMqConfig;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.example.pojo.bo.MessageBO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: chat-room
 * @description: 消息队列推送器
 * @author: stop.yc
 * @create: 2023-08-03 22:32
 **/
@Component
public class MqWriter {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Async
    public void pushWsMsg2Mq(MessageBO messageBO) {
        //封装消息确认报文
        MyMessageCorrelationData myMessageCorrelationData = BeanUtil.copyProperties(messageBO, MyMessageCorrelationData.class);
        //发送消息, ack和nak的逻辑在配置类中.
        rabbitTemplate.convertAndSend(WsMessageMqConfig.WS_EXCHANGE_NAME, "message.ws", JSONObject.toJSONString(messageBO), myMessageCorrelationData);
    }
}
