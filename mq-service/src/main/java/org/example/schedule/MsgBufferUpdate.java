package org.example.schedule;

import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisKey;
import org.example.dao.MsgReader;
import org.example.dao.MsgWriter;
import org.example.pojo.vo.WsMessageVO;
import org.example.route.MessageBuffer;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @program: util
 * @description: rabbitMq定时监听器
 * @author: stop.yc
 * @create: 2023-07-11 09:54
 **/
@Component
@Slf4j
@EnableScheduling
public class MsgBufferUpdate {

    @Resource
    private MsgReader msgReader;

    @Resource
    private MsgWriter msgWriter;

    // 每天0点执行一次
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateMsgBuffer() {
        List<String> keys = msgReader.keys(RedisKey.MESSAGE_KEY);
        log.info("keys 为: {}", keys);
        for (String key : keys) {
            String[] split = key.split(":");
            String userId = split[1];
            Set<ZSetOperations.TypedTuple<Object>> windowMsg = msgReader.getWindowMsg(RedisKey.MESSAGE_KEY, userId, Long.MAX_VALUE, 0, MessageBuffer.BUFFER_SIZE + 2, WsMessageVO.class);
            if (windowMsg.size() <= MessageBuffer.BUFFER_SIZE) {
                continue;
            }
            ZSetOperations.TypedTuple<Object> lastOne = CollectionUtils.lastElement(windowMsg);
            if (lastOne == null || lastOne.getScore() == null) {
                continue;
            }
            msgWriter.delWindowMsg(RedisKey.MESSAGE_KEY, userId, 0, lastOne.getScore());
        }
    }
}
