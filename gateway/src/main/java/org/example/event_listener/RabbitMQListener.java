package org.example.event_listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.config.RabbitMQConfig;
import org.example.util.hashring_util.HashRing;
import org.example.util.hashring_util.Server;
import org.example.util.hashring_util.support.HashRingRedis;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * @program: monitor
 * @description: 消息队列监听器
 * @author: stop.yc
 * @create: 2023-02-14 21:14
 **/
@Component
@Slf4j
public class RabbitMQListener {

    /**
     * 最大重试次数
     */
    @Value("${setting.max-retry-time}")
    private int MAX_RETRY_TIME;

    /**
     * 重试的时间间隔
     */
    @Value("${setting.retry-interval}")
    private int RETRY_INTERVAL;

    @Resource
    @Qualifier("websocketServer")
    private Server server;


    /**
     * 监听boot_queue队列,失败进行重试,一直失败进入死信队列.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void onMessage(Message message, Channel channel) throws Exception {

        int retryCount = 0;

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        boolean isSuccessful = false;
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();

            String msg = new String(message.getBody(), StandardCharsets.UTF_8);

            // 1.接受消息
            log.info(" 队列 {} 接受到消息: {}", RabbitMQConfig.QUEUE_NAME, msg);

            // 如果是hash环
            if (routingKey.contains("hashring")) {
                log.info("gateway中的rabbitmq监听到hash发生变动,开始重新读取");
                //2. 处理业务逻辑
                isSuccessful = doBusiness(msg);

                //业务未成功
                //4.进行业务重试
                while (retryCount < MAX_RETRY_TIME && !isSuccessful) {
                    //4.1间隔一定时间
                    Thread.sleep(RETRY_INTERVAL * 1000);

                    //4.2重试次数++
                    retryCount++;

                    log.info("队列 {} 执行业务失败! 开始进行第 {} 次重试", RabbitMQConfig.QUEUE_NAME, retryCount);

                    //4.3 重试业务
                    isSuccessful = doBusiness(msg);
                }
            }

            isSuccessful = true;
            //防止业务处理的方法未能捕获业务异常
        } catch (Exception e) {
            //未知异常!
            log.error("队列 {} 业务执行过程中发生了未知错误!", RabbitMQConfig.QUEUE_NAME, e);
        } finally {
            if (isSuccessful) {
                // 业务成功 手动签收
                channel.basicAck(deliveryTag, true);
            } else {
                //业务失败
                // 决绝签收,第三个参数为true重回队列,如果配置死信队列,必须设置为false
                channel.basicNack(deliveryTag, true, false);
                log.info("队列 {} 重试后依旧失败,进入死信队列", RabbitMQConfig.QUEUE_NAME);
            }
        }
    }


    /**
     * 死信队列监听,进行如入库等操作
     */
    @RabbitListener(queues = RabbitMQConfig.DLX_QUEUE_NAME)
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


    /**
     * 执行死信队列的业务处理(如入库)
     */
    private boolean doDLXBusiness(String message) {
        try {
            log.info("死信队列消息入库!");

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 执行正常队列的业务处理
     */
    private boolean doBusiness(String msg) {
        try {
            //hashUtils.updateHashRing();
            HashRing hashRingRedis = HashRingRedis.newInstance(server);
            hashRingRedis.updateHashRingFromCache();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
