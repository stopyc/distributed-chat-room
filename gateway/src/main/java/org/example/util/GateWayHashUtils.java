package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.example.util.RedisUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.example.constant.RedisKey.PREFIX_HASH_RING;

/**
 * @program: chat-room
 * @description: 一致性哈希算法实现类
 * @author: stop.yc
 * @create: 2023-04-30 16:53
 **/
@Slf4j
@Component
public class GateWayHashUtils {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
     */
    private static final SortedMap<Double, String> virtualNodes = new TreeMap<>();

    private static final Object mutex = new Object();

    public void getHashRingFromRedis() {
        log.info("gateway获取rsedi中的哈希环!");
        // 启动时,获取哈希环,供gateway使用
        Set<ZSetOperations.TypedTuple<Object>> tupleSet = redisTemplate.opsForZSet().rangeByScoreWithScores(PREFIX_HASH_RING, 0.0, Double.MAX_VALUE);
        if (CollectionUtils.isEmpty(tupleSet)) {
            log.info("redis中哈希环为空, 当前无ws服务启动或nacos未启动开始初始化!");
            return;
        }
        log.info("gateway获取redis中的哈希环中每个ws节点位置为:");
        for (ZSetOperations.TypedTuple<Object> tuple : tupleSet) {
            String ipPort = (String) tuple.getValue();
            double hash = tuple.getScore();
            log.info("hash值为: {}, ip:port为: {}", hash, ipPort);
            virtualNodes.put(hash, ipPort);
        }
    }

    /**
     * 更新哈希环
     */
    public void updateHashRing() {
        synchronized (mutex) {
            log.info("哈希环更新,从redis中获取哈希环");
            // 启动时,获取哈希环,供gateway使用
            Set<ZSetOperations.TypedTuple<Object>> tupleSet = redisTemplate.opsForZSet().rangeByScoreWithScores(PREFIX_HASH_RING, 0.0, Double.MAX_VALUE);

            if (CollectionUtils.isEmpty(tupleSet)) {
                log.info("redis中哈希环为空, 当前无ws服务启动或nacos未启动开始初始化!");
                return;
            }
            // 清空哈希环
            virtualNodes.clear();
            log.info("gateway获取最新的redis中的哈希环中每个ws节点位置为:");
            for (ZSetOperations.TypedTuple<Object> tuple : tupleSet) {
                String ipPort = (String) tuple.getValue();
                double hash = tuple.getScore();
                log.info("hash值为: {}, ip:port为: {}", hash, ipPort);
                virtualNodes.put(hash, ipPort);
            }
        }
    }

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     * @param str :key Str
     * @return :hashCode
     */
    public static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    /**
     * 得到应当路由到的结点
     * @param key: 自定义,一般为用户id
     * @return:  ipPort
     */
    public static String getServer(String key) {
        synchronized (mutex) {
            if (virtualNodes.size() == 0) {
                return null;
            }
            //得到该key的hash值
            int hash = getHash(key);
            // 得到大于该Hash值的所有Map
            SortedMap<Double, String> subMap = virtualNodes.tailMap((double)hash);
            String virtualNode;
            if (subMap.isEmpty()) {
                //如果没有比该key的hash值大的，则从第一个node开始
                Double i = virtualNodes.firstKey();
                //返回对应的服务器
                virtualNode = virtualNodes.get(i);
            } else {
                //第一个Key就是顺时针过去离node最近的那个结点
                Double i = subMap.firstKey();
                //返回对应的服务器
                virtualNode = subMap.get(i);
            }
            return virtualNode;
        }
    }
}
