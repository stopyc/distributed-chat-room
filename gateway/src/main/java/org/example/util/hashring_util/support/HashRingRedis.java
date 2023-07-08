package org.example.util.hashring_util.support;

import lombok.extern.slf4j.Slf4j;
import org.example.util.hashring_util.AbstractHashRing;
import org.example.util.hashring_util.Server;
import org.example.util.hashring_util.entity.Address;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Set;
import java.util.SortedMap;

/**
 * @program: util
 * @description: 哈希环存储于redis
 * @author: stop.yc
 * @create: 2023-07-06 16:12
 **/
@Slf4j
@Component
public class HashRingRedis extends AbstractHashRing {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    private HashRingRedis(Server server) {
        this.server = server;
    }

    private HashRingRedis() {

    }
    @PostConstruct
    public void init(){
        instance = this;
        instance.redisTemplate = this.redisTemplate;
    }

    private static HashRingRedis instance = new HashRingRedis();

    public static HashRingRedis newInstance(Server server) {
        instance.server = server;
        return instance;
    }

    @Override
    public SortedMap<Double, Address> getHashRingFromCache() {
        return getHashRingFromRedis();
    }

    private SortedMap<Double, Address> getHashRingFromRedis() {
        synchronized (MUTEX) {
            log.info("gateway获取redis中的哈希环!");
            // 启动时,获取哈希环,供gateway使用
            Set<ZSetOperations.TypedTuple<Object>> tupleSet = redisTemplate.opsForZSet().rangeByScoreWithScores(PREFIX_HASH_RING, 0.0, Double.MAX_VALUE);
            if (CollectionUtils.isEmpty(tupleSet)) {
                log.info("redis中哈希环为空, 当前无ws服务启动或Nacos未启动开始初始化!");
                return virtualNodes;
            }
            log.info("gateway获取redis中的哈希环中每个ws节点位置为:");
            for (ZSetOperations.TypedTuple<Object> tuple : tupleSet) {
                Address ipPort = (Address) tuple.getValue();
                double hash = tuple.getScore();
                log.info("hash值为: {}, ip:port为: {}", hash, ipPort);
                virtualNodes.put(hash, ipPort);
            }
            return virtualNodes;
        }
    }
}
