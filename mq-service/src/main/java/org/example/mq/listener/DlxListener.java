package org.example.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.config.DlxMqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @program: chat-room
 * @description: 死信队列监听器
 * @author: stop.yc
 * @create: 2023-07-13 11:41
 **/
@Component
@Slf4j
public class DlxListener {
    @RabbitListener(queues = DlxMqConfig.DLX_QUEUE_NAME)
    public void onDLXMessage(Message message, Channel channel) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            // 1.接受消息,日志记录后,进行相应处理
            log.info("死信队列接受消息! {} ", msg);

            // 2. 执行死信队列的业务处理,比如把死信进行入库操作
            boolean isSuccessful = doDLXBusiness(msg);

            // 业务成功 手动签收
            channel.basicAck(deliveryTag, true);
            //防止业务处理的方法未能捕获业务异常
        } catch (Exception e) {
            //未知异常!需要重回队列,重试,进行业务操作
            channel.basicNack(deliveryTag, true, true);
        }
    }

    private boolean doDLXBusiness(String msg) {
        return true;
    }
}
