package org.example.dao;

import org.example.pojo.dto.ScrollingPaginationDTO;
import org.example.util.RedisNewUtil;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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
        Set<T> zget = RedisNewUtil.zget(redisPrefix, key, score, tClass);
        return zget != null && zget.size() > 0;
    }

    public <T> ScrollingPaginationDTO<T> getMsg(String redisPrefix, Object key, double max, long offset, long count, Class<T> tClass) {
        return RedisNewUtil.zget(redisPrefix + key.toString(), 0, max, offset, count, tClass);
    }

    public <T> T getAckMsg(String redisPrefix, Object key, Class<T> tClass) {
        return RedisNewUtil.get(redisPrefix,
                key.toString(),
                tClass);
    }

    public Set<ZSetOperations.TypedTuple<Object>> getWindowMsg(String redisPrefix, Object key, long max, long offset, long count, Class<?> tClass) {
        return RedisNewUtil.zget(redisPrefix, key.toString(), max, offset, count, tClass);
    }

    public <T> T getDurableMsgByScore(String redisPrefix, Object key, long score, Class<T> tClass) {
        Set<T> durableMsg = RedisNewUtil.zget(redisPrefix, key.toString(), score, tClass);
        if (CollectionUtils.isEmpty(durableMsg)) {
            return null;
        }
        List<T> list = new ArrayList<>(durableMsg);
        return list.get(0);
    }


}
