package org.example.mq.correlationData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @program: chat-room
 * @description: 自定义确认消息
 * @author: stop.yc
 * @create: 2023-07-20 10:09
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyMessageCorrelationData extends CorrelationData {

    private Long messageId;

    private Long clientMessageId;

    private Long fromUserId;

    private Long serverTime;

    private Long clientTime;
}
