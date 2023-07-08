package org.example.util;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.RedisData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: chat-room
 * @description: redis工具类
 * @author: stop.yc
 * @create: 2023-02-03 17:36
 **/
@Slf4j
@Component
public class RedisUtils {


    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    private static final Long BEGIN_TIME = 1672531200L;

    private static final String DISTRIBUTED_LOCK_PREFIX = "distributed:lock:";

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setResultType(Long.class);
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));

        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setResultType(Long.class);
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
    }

    /**
     * 设置缓存
     *
     * @param key        :key
     * @param data       :value
     * @param expireTime :过期时间
     * @param unit       :时间单位
     */
    public void set(final String key, final Object data, Long expireTime, TimeUnit unit) {
        redisTemplate.opsForValue()
                .set(key, data, expireTime, unit);
    }


    /**
     * 设置缓存,不设置过期时间
     *
     * @param key  :key
     * @param data :value
     */
    public void set(final String key, final Object data) {
        redisTemplate.opsForValue()
                .set(key, data);
    }


    /**
     * 设置缓存,加入随机时间,防止缓存雪崩
     *
     * @param key        :key
     * @param data       :value
     * @param expireTime :过期时间
     * @param randomTime :随机时间
     * @param unit       :时间单位
     */
    public void setWithRandomTime(final String key, final Object data, Long expireTime, Long randomTime, TimeUnit unit) {
        redisTemplate.opsForValue()
                .set(key, data, expireTime + new Random().nextInt(Math.toIntExact(randomTime)), unit);
    }

    /**
     * 设置缓存,逻辑过期时间
     *
     * @param key        :key
     * @param data       :value
     * @param expireTime :逻辑过期时间
     * @param unit       :时间单位
     */
    public void setLogicalTime(final String key, final Object data, Long expireTime, TimeUnit unit) {
        RedisData redisData = new RedisData();

        redisData.setData(data);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(expireTime)));

        redisTemplate.opsForValue()
                .set(key, redisData);
    }


    /**
     * 设置空值,防止缓存穿透(过期时间必须设置,所有没有重载方法)
     *
     * @param key:key
     * @param expireTime :过期时间
     * @param unit       :时间单位
     */
    public void setNull(final String key, Long expireTime, TimeUnit unit) {
        redisTemplate.opsForValue()
                .set(key, "", expireTime, unit);
    }


    /**
     * 获取缓存对象
     *
     * @param key  :key
     * @param type :类型
     * @param <T>  :类型
     * @return :对象
     */
    public <T> T get(final String key, Class<T> type) {
        Object o = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(o)) {
            return null;
        }

        String dataStr = o.toString();

        if (!StringUtils.hasText(dataStr)) {
            return null;
        }

        T t = JSONObject.parseObject(dataStr, type);
        if (Objects.isNull(t)) {
            return null;
        }
        return t;
    }

    /**
     * 缓存Map
     * @param key 键值
     * @param dataMap 缓存的Map
     */
    public void setMap(final String key, final Map<Object, Object> dataMap, Integer expireTime, TimeUnit unit) {
        setRedisMap(key, dataMap);
    }

    public void setMap(final String key, final Map<Integer, String> dataMap) {
        setRedisMap(key, dataMap);
    }

    private void setRedisMap(String key, Map dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 缓存Map
     * @param key 键值
     * @param data 缓存的Map
     */
    public void setMap(final String key, final String hashKey, final Object data) {

        System.out.println("redisTemplate.getKeySerializer().getClass() = " + redisTemplate.getKeySerializer().getClass());
        System.out.println("redisTemplate.getKeySerializer().getClass() = " + redisTemplate.getValueSerializer().getClass());
        System.out.println("redisTemplate.getKeySerializer().getClass() = " + redisTemplate.getHashKeySerializer().getClass());
        System.out.println("redisTemplate.getKeySerializer().getClass() = " + redisTemplate.getHashValueSerializer().getClass());

        if (data != null) {
            redisTemplate.opsForHash().put(key, hashKey, data);
        }
    }

    /**
     * 获得缓存的Map
     * @param key 键值
     * @return  缓存的Map
     */
    public Map<Object, Object> getMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void delMapValue(final String key, final String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public Object getMapValue(final String key, final String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public void setCacheSet(final String key, Object data ) {
        redisTemplate.opsForSet()
                .add(key, data);
    }

    public void setCacheSet(final String key, Object data , Integer expireTime, TimeUnit unit ) {
        redisTemplate.opsForSet()
                .add(key, data);
        redisTemplate.expire(key, expireTime, unit);
    }

    public void setSortedSet(final String key, Object data, double score, Integer expireTime, TimeUnit unit ) {
        redisTemplate.opsForZSet().add(key, data, score);
        redisTemplate.expire(key, expireTime, unit);
    }

    public Set<Object> getCacheSet(final String key) {
        return redisTemplate.opsForSet()
                .members(key);
    }

    public Set<ZSetOperations.TypedTuple<Object>> getSortedSet(final String key, double min, double max, long offset, long count ) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max, offset, count);
    }

    public List<Object> getCacheList(String key) {

        Set<Object> keys = redisTemplate.keys(key + "*");

        if (keys == null) {
            return Collections.emptyList();
        }

        return redisTemplate.opsForValue().multiGet(keys);
    }

    public List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, redisTemplate.opsForList().size(key));
    }

    public Set<Object> getKeysByPrefix(String prefix) {
        return redisTemplate.keys(prefix + "*");
    }

    /**
     * 获取逻辑缓存对象
     *
     * @param key :key
     * @return :对象
     */
    public RedisData getLogicalInstance(final String key) {
        Object o = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(o)) {
            return null;
        }

        String dataStr = o.toString();

        if (!StringUtils.hasText(dataStr)) {
            return null;
        }

        RedisData t = JSONObject.parseObject(dataStr, RedisData.class);
        if (Objects.isNull(t)) {
            return null;
        }
        return t;
    }


    /**
     * 获取逻辑缓存对象中的对象
     */
    public <T> T getByLogicInstance(final RedisData redisData, Class<T> type) {
        if (Objects.isNull(redisData) || Objects.isNull(redisData.getData())) {
            return null;
        }

        T t = JSONObject.parseObject(redisData.getData().toString(), type);
        if (Objects.isNull(t)) {
            return null;
        }
        return t;
    }

    /**
     * 尝试获取互斥锁(这个锁的过期时间不能太长,防止阻塞)
     *
     * @param key        :锁key
     * @param expireTime :过期时间
     * @return :获取是否成功
     */
    public boolean tryLock(String key, Long expireTime, TimeUnit unit) {

        //其实获取锁,运用redis进行解决的思路是利用setNx,如果存在就不能写入的特性
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, "1", expireTime, unit);

        //这里调用外部第三方,防止Boolean拆箱报NPE
        return BooleanUtil.isTrue(flag);
    }


    /**
     * 解除互斥锁(必须放在finally块中执行)
     *
     * @param key :锁key
     * @return :成功与否
     */
    public boolean unlock(String key) {

        Boolean flag = redisTemplate.delete(key);

        return BooleanUtil.isTrue(flag);
    }

    /**
     * 判断逻辑过期对象是否过期
     *
     * @param key
     * @param redisData
     * @return
     */
    public boolean isLogicalExpiration(String key, RedisData redisData) {
        //如果当前时间是在过期时间之后,表示过期
        return LocalDateTime.now().isAfter(redisData.getExpireTime());
    }

    // * 缓存穿透
    // *
    // * @param keyPrefix  :key前缀
    // * @param id         :id,为null的时候查询全部
    // * @param type       :查询的类型
    // * @param dbFallback :回调函数
    // * @param time       :过期时间
    // * @param unit       :时间单位
    // * @param <R>        :返回类型
    // * @param <ID>       :id类型
    // * @return :返回查询对象
    // */
    //public <R, ID> R queryWithPassThrough(
    //        String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
    //
    //    String key = keyPrefix + id;
    //
    //    // 1.从redis查询缓存
    //    Object instance = redisTemplate.opsForValue().get(key);
    //
    //    // 2.判断是否存在
    //    if (!Objects.isNull(instance) && StringUtils.hasText(instance.toString())) {
    //        // 3.存在，直接返回
    //        return JSONObject.parseObject(instance.toString(), type);
    //    }
    //
    //    //如果不为null,那么表示命中了空值
    //    if (!Objects.isNull(instance)) {
    //        return null;
    //    }
    //
    //    // 4.不存在，查询数据库
    //    R r = dbFallback.apply(id);
    //
    //    // 5.不存在，缓存空值,返回null
    //    if (Objects.isNull(r)) {
    //        // 将空值写入redis
    //        redisTemplate.opsForValue().set(key, "", time, unit);
    //        // 返回错误信息
    //        return null;
    //    }
    //
    //    // 6.存在，写入redis
    //    this.set(key, r, time, unit);
    //
    //    return r;
    //}
    //
    ///**
    // * 缓存击穿--逻辑过期(这个方法必须建立在有缓存预热的前提下)
    // *
    // * @param keyPrefix  :key前缀
    // * @param id         :id,为null的时候查询全部
    // * @param type       :查询的类型
    // * @param dbFallback :回调函数
    // * @param time       :过期时间
    // * @param unit       :时间单位
    // * @param <R>        :返回类型
    // * @param <ID>       :id类型
    // * @return :返回查询对象
    // */
    //public <R, ID> R queryWithLogicalExpire(
    //        String keyPrefix, ID id, Class<R> type, String lockKeyPrefix, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
    //
    //    String key = keyPrefix + id;
    //
    //    // 1.从redis查询商铺缓存
    //    Object instance = redisTemplate.opsForValue().get(key);
    //
    //    // 2.判断是否存在
    //    if (Objects.isNull(instance)) {
    //        // 3.不存在，直接返回
    //        return null;
    //    }
    //
    //
    //    // 4.命中，需要先把json反序列化为对象
    //    RedisData redisData = JSONObject.parseObject(instance.toString(), RedisData.class);
    //
    //    R r = JSONObject.parseObject(redisData.getData().toString(), type);
    //
    //    LocalDateTime expireTime = redisData.getExpireTime();
    //
    //    // 5.判断是否过期
    //    if (expireTime.isAfter(LocalDateTime.now())) {
    //        // 5.1.未过期，直接返回店铺信息
    //        return r;
    //    }
    //    // 5.2.已过期，需要缓存重建
    //    // 6.缓存重建
    //    // 6.1.获取互斥锁
    //    String lockKey = lockKeyPrefix + id;
    //
    //    boolean isLock = tryLock(lockKey, 10L, TimeUnit.SECONDS);
    //    // 6.2.判断是否获取锁成功
    //    if (isLock) {
    //        // 6.3.成功，开启独立线程，实现缓存重建
    //        CACHE_REBUILD_EXECUTOR.submit(() -> {
    //            try {
    //
    //                // 1.从redis查询商铺缓存
    //                Object instance2 = redisTemplate.opsForValue().get(key);
    //
    //                // 2.判断是否存在
    //                if (Objects.isNull(instance2)) {
    //                    // 3.不存在，直接返回
    //                    return;
    //                }
    //
    //
    //                // 4.命中，需要先把json反序列化为对象
    //                RedisData redisData2 = JSONObject.parseObject(instance2.toString(), RedisData.class);
    //
    //                LocalDateTime expireTime2 = redisData2.getExpireTime();
    //
    //                // 5.判断是否过期
    //                if (expireTime2.isAfter(LocalDateTime.now())) {
    //                    // 5.1.未过期，表示是后面获取锁的线程
    //                    return;
    //                }
    //                // 查询数据库
    //                R newR = dbFallback.apply(id);
    //                // 重建缓存
    //                this.setLogicalTime(key, newR, time, unit);
    //            } catch (Exception e) {
    //                throw new RuntimeException(e);
    //            } finally {
    //                // 释放锁
    //                unlock(lockKey);
    //            }
    //        });
    //    }
    //    // 6.4.返回过期的商铺信息
    //    return r;
    //}
    //
    //
    //public <R, ID> R queryWithMutex(
    //        String keyPrefix, ID id, Class<R> type, String lockKeyPrefix, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
    //
    //    String key = keyPrefix + id;
    //
    //    // 1.从redis查询商铺缓存
    //    Object instance = redisTemplate.opsForValue().get(key);
    //    // 2.判断是否存在
    //    if (!Objects.isNull(instance) && StringUtils.hasText(instance.toString())) {
    //        // 3.存在，直接返回
    //        return JSONObject.parseObject(instance.toString(), type);
    //    }
    //
    //    //如果不为null,那么表示命中了空值
    //    if (!Objects.isNull(instance)) {
    //        return null;
    //    }
    //
    //    // 4.实现缓存重建
    //    // 4.1.获取互斥锁
    //    String lockKey = lockKeyPrefix + id;
    //    R r = null;
    //    try {
    //        // 4.2.判断是否获取成功
    //        while (!tryLock(lockKey, 10L, TimeUnit.SECONDS)) {
    //            // 4.3.获取锁失败，休眠并重试
    //            Thread.sleep(50);
    //        }
    //
    //        //二次检查
    //
    //        // 1.从redis查询商铺缓存
    //        Object instance2 = redisTemplate.opsForValue().get(key);
    //        // 2.判断是否存在
    //        if (!Objects.isNull(instance2) && StringUtils.hasText(instance2.toString())) {
    //            // 3.存在，直接返回
    //            return JSONObject.parseObject(instance2.toString(), type);
    //        }
    //
    //        //如果不为null,那么表示命中了空值
    //        if (!Objects.isNull(instance2)) {
    //            return null;
    //        }
    //
    //        // 4.4.获取锁成功，根据id查询数据库
    //        r = dbFallback.apply(id);
    //        // 5.不存在，返回错误
    //        if (r == null) {
    //            // 将空值写入redis
    //            redisTemplate.opsForValue().set(key, "", time, unit);
    //            // 返回错误信息
    //            return null;
    //        }
    //        // 6.存在，写入redis
    //        this.set(key, r, time, unit);
    //    } catch (InterruptedException e) {
    //        throw new RuntimeException(e);
    //    } finally {
    //        // 7.释放锁
    //        unlock(lockKey);
    //    }
    //    // 8.返回
    //    return r;
    //}

    public Long getId(String idPrefix) {

        //首先获取当前距离某个时间的时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowTime = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowTime - BEGIN_TIME;

        //获取当前日期
        String nowDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //然后获取redis自增序列号
        Long increment = redisTemplate.opsForValue().increment("icr:" + idPrefix + nowDate + ":");

        long redisId = 0;
        if (increment != null) {
            redisId = increment;
        }

        //拼接后返回
        return timeStamp << 32 | redisId;
    }


    /**
     * 获取分布式锁,过期时间单位规定为秒,避免过长时间
     *
     * @param key        :key
     * @param expireTime : 过期时间
     * @return :获取锁是否成功
     */
    public boolean tryDistributedLock(String key, long expireTime) {

        String  threadId = ID_PREFIX + Thread.currentThread().getId();

        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(DISTRIBUTED_LOCK_PREFIX + key, threadId , expireTime, TimeUnit.SECONDS);

        return BooleanUtil.isTrue(result);
    }


    /**
     * 解锁分布式锁,这样写,不能保证原子性,因为是通过查询后做逻辑性处理,我们用lua脚本进行改进
     * @param key :key
     * @return :成功与否
     *//*
    public boolean unDistributedLock(String key) {

        //获取线程标识
        String threadId = ID_PREFIX + Thread.currentThread().getId();

        //获取锁中表示
        Object id = redisTemplate.opsForValue().get(DISTRIBUTED_LOCK_PREFIX + key);

        //判断标识是否一致
        Boolean result = null;
        if (threadId.equals(id)) {

            result = redisTemplate.delete(DISTRIBUTED_LOCK_PREFIX + key);
        }

        return BooleanUtil.isTrue(result);
    }*/

    /**
     * 解锁分布式锁,使用lua脚本
     * @param key :key
     * @return :成功与否
     */
    public void unDistributedLock(String key) {
        //调用lua脚本
        redisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(DISTRIBUTED_LOCK_PREFIX + key),
                ID_PREFIX + Thread.currentThread().getId());
    }

}
