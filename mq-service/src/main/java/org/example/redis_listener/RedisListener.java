package org.example.redis_listener;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.config.WsMessageMqConfig;
import org.example.constant.RedisConstant;
import org.example.constant.RedisKey;
import org.example.mq.correlationData.MyMessageCorrelationData;
import org.example.pojo.bo.MessageBO;
import org.example.pojo.exception.SystemException;
import org.example.util.RedisNewUtil;
import org.example.websocket.GlobalWsMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author YC104
 * @description: Redis过期事件监听类，也就是给消息定一个超时时间，如果超时了，进行消息重试。
 */
@Slf4j
@Component
public class RedisListener implements MessageListener {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("失效的redis是:" + expiredKey);
        RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
        String channel = String.valueOf(serializer.deserialize(message.getChannel()));
        String body = String.valueOf(serializer.deserialize(message.getBody()));
        //key过期监听,在处理业务之前校验下自己业务的key和监听的key以及库号
        if (channel.contains("__:expired") && body.contains(RedisKey.ACK_MESSAGE_KEY)) {
            String[] keySplit = body.split(":");
            if (keySplit.length != 4) {
                return;
            }
            //1. 获取到过期的消息key
            //表示该条消息已经超时了,需要重新发送给交换机
            String userIdStr = keySplit[2];
            String clientMessageIdStr = keySplit[3];


            //3. 首先查看该条消息的发送者是不是在这台服务器上
            if (GlobalWsMap.isOnline(Long.parseLong(userIdStr))) {
                log.info("ack队列中的用户id 为 {} 消息id为 {} 的消息已经发送超时,需要重新发送到交换机中!", userIdStr, clientMessageIdStr);
                //3.1 是的话就从持久队列中重新获取,然后推送给交换机,并重新声明ack消息
                Set<MessageBO> durableMsg = RedisNewUtil.zget(RedisKey.MESSAGE_KEY, userIdStr, Long.parseLong(clientMessageIdStr), MessageBO.class);
                if (durableMsg.size() > 1) {
                    throw new SystemException("redis中的消息队列中的客户端消息id 为 " + clientMessageIdStr + " 不止一条,请检查!");
                }
                for (MessageBO messageBO : durableMsg) {
                    RedisNewUtil.put(RedisKey.ACK_MESSAGE_KEY,
                            messageBO.getFromUserId() + ":" + messageBO.getClientMessageId(),
                            messageBO,
                            RedisConstant.ACK_EXPIRATION_TIME,
                            TimeUnit.SECONDS);
                    push2Mq(messageBO);
                }
            }
        }

    }

    private void push2Mq(MessageBO messageBO) {
        //封装消息确认报文
        MyMessageCorrelationData myMessageCorrelationData = BeanUtil.copyProperties(messageBO, MyMessageCorrelationData.class);
        //发送消息, ack和nak的逻辑在配置类中.
        rabbitTemplate.convertAndSend(WsMessageMqConfig.WS_EXCHANGE_NAME, "message.ws", JSONObject.toJSONString(messageBO), myMessageCorrelationData);
    }
}