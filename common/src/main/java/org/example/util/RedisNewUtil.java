package org.example.util;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.ScrollingPaginationDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: chat-room
 * @description: 新的redis工具列
 * @author: stop.yc
 * @create: 2023-07-19 16:22
 **/
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class RedisNewUtil {
    private static final String LUA_INCR_EXPIRE =
            "local key,ttl=KEYS[1],ARGV[1] \n" +
                    " \n" +
                    "if redis.call('EXISTS',key)==0 then   \n" +
                    "  redis.call('SETEX',key,ttl,1) \n" +
                    "  return 1 \n" +
                    "else \n" +
                    "  return tonumber(redis.call('INCR',key)) \n" +
                    "end ";
    private static RedisTemplate redisTemplate;

    static {
        RedisNewUtil.redisTemplate = SpringUtil.getBean("objectRedisTemplate", RedisTemplate.class);
    }

    public static <T> List<T> multiGet(Collection<String> keys, Class<T> tClass) {
        List<Object> list = redisTemplate.opsForValue().multiGet(keys);
        if (Objects.isNull(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(o -> toBeanOrNull(o, tClass)).collect(Collectors.toList());
    }

    public static Long inc(String key, int time, TimeUnit unit) {
        RedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_INCR_EXPIRE, Long.class);
        Object execute = redisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(unit.toSeconds(time)));
        return (Long) execute;
    }

    static <T> T toBeanOrNull(Object json, Class<T> tClass) {
        return json == null ? null : JSONObject.parseObject((String) json, tClass);
    }

    public static void sput(String redisPrefix, Object key, Object object) {
        redisTemplate.opsForSet().add(redisPrefix + key.toString(), JSONObject.toJSONString(object));
    }

    public static <T> Set<T> sget(String redisPrefix, Object key, Class<T> tClass) {
        Set<Object> members = redisTemplate.opsForSet().members(redisPrefix + key.toString());
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptySet();
        }
        return members.parallelStream().map(o -> toBeanOrNull(o, tClass)).collect(Collectors.toSet());
    }

    public static void zput(String redisPrefix, Object key, Object object, double score) {
        redisTemplate.opsForZSet().add(redisPrefix + key.toString(), JSONObject.toJSONString(object), score);
    }

    public static void mput(String redisPrefix, Object key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(redisPrefix + key.toString(), hashKey.toString(), JSONObject.toJSONString(value));
    }

    public static void mput(String redisPrefix, Object key, Object hashKey, Object value, long time, TimeUnit unit) {
        redisTemplate.opsForHash().put(redisPrefix + key.toString(), hashKey.toString(), JSONObject.toJSONString(value));
        redisTemplate.expire(redisPrefix + key.toString() + hashKey.toString(), time, unit);
    }

    public static void put(String redisPrefix, Object key, Object value, long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(redisPrefix + key.toString(), JSONObject.toJSONString(value), time, unit);
    }

    public static void del(String redisPrefix, Object key) {
        redisTemplate.delete(redisPrefix + key.toString());
    }

    public static void put(String redisPrefix, Object key, Object value) {
        redisTemplate.opsForValue().set(redisPrefix + key.toString(), JSONObject.toJSONString(value));
    }

    public static <T> T get(String redisPrefix, Object key, Class<T> tClass) {
        Object o = redisTemplate.opsForValue().get(redisPrefix + key.toString());
        if (Objects.isNull(o)) {
            return null;
        }
        return toBeanOrNull(o, tClass);
    }

    public static <T> T mget(String redisPrefix, Object key, Object hashKey, Class<T> tClass) {
        Object o = redisTemplate.opsForHash().get(redisPrefix + key.toString(), hashKey.toString());
        return toBeanOrNull(o, tClass);
    }

    public static <T> Set<T> zget(String redisPrefix, Object key, double score, Class<T> tClass) {
        Set<Object> set = redisTemplate.opsForZSet().rangeByScore(redisPrefix + key.toString(), score, score);
        if (CollectionUtils.isEmpty(set)) {
            return Collections.emptySet();
        }
        return set.parallelStream().map(o -> toBeanOrNull(o, tClass)).collect(Collectors.toSet());
    }

    public static void expire(String redisPrefix, Object key, long time, TimeUnit unit) {
        redisTemplate.expire(redisPrefix + key.toString(), time, unit);
    }

    public static <T> ScrollingPaginationDTO<T> zget(final String key, double min, double max, long offset, long count, Class<T> tClass) {
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count);
        if (CollectionUtils.isEmpty(set)) {
            ScrollingPaginationDTO<T> tScrollingPaginationDTO = new ScrollingPaginationDTO<>();
            tScrollingPaginationDTO.setResultList(Collections.emptyList());
            return tScrollingPaginationDTO;
        }
        long curMax = 0;
        long curCount = 1;
        Map mapValue;
        Long userId = SecurityUtils.getUser().getUserId();
        T temp = null;
        List<T> resultSet = new ArrayList<>(set.size());
        for (ZSetOperations.TypedTuple<Object> tuple : set) {
            Object jsonString = tuple.getValue();
            T obj = toBeanOrNull(jsonString, tClass);
            resultSet.add(obj);
            if (curMax == tuple.getScore().longValue()) {
                curCount++;
            } else {
                curMax = tuple.getScore().longValue();
                curCount = 1;
            }
        }
        return ScrollingPaginationDTO.<T>builder().resultList(resultSet).offset(curCount).max(curMax).build();
    }
}
