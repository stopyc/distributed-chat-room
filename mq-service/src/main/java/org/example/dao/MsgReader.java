package org.example.dao;

import org.example.constant.RedisKey;
import org.example.pojo.dto.ScrollingPaginationDTO;
import org.example.util.RedisNewUtil;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @program: chat-room
 * @description: 消息读取服务
 * @author: stop.yc
 * @create: 2023-07-31 16:41
 **/
@Component
public class MsgReader {
    public <T> boolean hasMsg(String redisPrefix, Object key, double score, Class<T> tClass) {
        Set<T> zget = RedisNewUtil.zget(RedisKey.SINGLE_CHAT, key, score, tClass);
        return zget != null && zget.size() > 0;
    }

    public <T> ScrollingPaginationDTO<T> getMsg(String redisPrefix, Object key, double max, long offset, Class<T> tClass) {
        return RedisNewUtil.zget(redisPrefix + key.toString(), 0, max, offset, 10, tClass);
    }
}
