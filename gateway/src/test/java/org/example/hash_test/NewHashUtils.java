/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.hash_test;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @program: nacos-all
 * @description:
 * @author: stop.yc
 * @create: 2023-05-03 16:33
 **/


@Slf4j
@Component
public class NewHashUtils {


    private static final String WS_SERVICE_NAME = "ws-service";

    private static final String HASH_RING_KEY = "hashring:";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * According to the number of real nodes and virtual nodes of the service, get the hash ring
     *
     * @param realNodes         :realServiceCollection
     * @param virtualNodesCount :Number of virtual nodes (0 indicates no need for virtual nodes, -1 indicates the default of 3 virtual nodes)
     * @return :hashRingKeyHashValueValueIpPort
     */
    //public SortedMap<Object, Object> getHashRing(List<? extends Instance> realNodes, int virtualNodesCount) {
    //
    //    if (virtualNodesCount == -1) {
    //        virtualNodesCount = 3;
    //    }
    //
    //    System.out.println("realNodes = " + realNodes);
    //    SortedMap<Object, Object> virtualNodes = new TreeMap<>();
    //
    //    for (Instance realNode : realNodes) {
    //
    //        if (!realNode.isHealthy() || !realNode.isEnabled()) {
    //            continue;
    //        }
    //        SortedMap<Object, Object> sortedMap = getOneHashRing(realNode.getIp(), realNode.getPort(), 5);
    //
    //        virtualNodes.putAll(sortedMap);
    //    }
    //
    //    return int2ObjMap(virtualNodes);
    //}

    /**
     * 获取单个服务的哈希环
     *
     * @param ip                :ip
     * @param port              :端口
     * @param virtualNodesCount :虚拟结点个数
     * @return :treeMap
     */
    private SortedMap<Object, Object> getOneHashRing(String ip, int port, int virtualNodesCount) {

        SortedMap<Object, Object> virtualNodes = new TreeMap<>();

        //2. obtainTheNameOfTheRealNode
        String realNodeName = ip + ":" + port;

        //3. obtainHashBasedOnName
        int realNodeHash = getHash(realNodeName);

        log.info("ws服务真实节点 ( {}:{} )的哈希值为 {}", ip, port, realNodeHash);

        virtualNodes.put(realNodeHash, realNodeName);

        for (int i = 0; i < virtualNodesCount; i++) {

            String virtualNodeName = realNodeName + "&&VN" + i;

            int hash = getHash(virtualNodeName);

            virtualNodes.put(hash, realNodeName);
        }

        return virtualNodes;
    }

    private Set<ZSetOperations.TypedTuple<Object>> getOneHashRing2(String ip, int port, int virtualNodesCount) {

        Set<ZSetOperations.TypedTuple<Object>> set = new TreeSet<>();

        //2. obtainTheNameOfTheRealNode
        String realNodeName = ip + ":" + port;

        //3. obtainHashBasedOnName
        int realNodeHash = getHash(realNodeName);

        log.info("ws服务真实节点 ( {}:{} )的哈希值为 {}", ip, port, realNodeHash);

        set.add(new DefaultTypedTuple<>(realNodeName, (double) realNodeHash));

        for (int i = 0; i < virtualNodesCount; i++) {

            String virtualNodeName = realNodeName + "&&VN" + i;

            int hash = getHash(virtualNodeName);

            set.add(new DefaultTypedTuple<>(virtualNodeName, (double)hash));
        }

        return set;
    }

    /**
     * 获取ws服务名称
     *
     * @return :服务名称
     */
    private String getWsServiceName() {
        return WS_SERVICE_NAME;
    }

    /**
     * 获取哈希环redis中的前缀
     *
     * @return :前缀
     */
    private String getHashRingKey() {
        return HASH_RING_KEY;
    }

    /**
     * 把map中的泛型进行转换成obj,才能存入redis
     *
     * @param map :哈希环
     * @return : obj哈希环
     */
    private SortedMap<Object, Object> int2ObjMap(SortedMap<Integer, String> map) {

        SortedMap<Object, Object> virtualNodes = new TreeMap<>();

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            virtualNodes.put(entry.getKey() + "", entry.getValue());
        }

        return virtualNodes;
    }


    /**
     * Using FNV 1_ 32_ The HASH algorithm calculates the hash value of the server without using the method of rewriting the hash code, resulting in no difference in the final effect
     *
     * @param str :1
     * @return :1
     */
    public int getHash(String str) {
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

        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    /**
     * 服务上线,或者健康状态好转
     *
     * @param namespaceId :namespace
     * @param serviceName :服务名称
     * @param ip          :ip
     * @param port        :port
     * @param healthy     :是否健康
     * @param enabled     :是否可用
     */
    public void updateHashRingIsRegister(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {

        log.info(serviceName + "请求服务注册, ip端口为: " + ip + ":" + port + "是否健康: " + (healthy ? "是 " : "否 ") + " 是否可用: " + (enabled ? "是" : "否"));

        //有新注册服务,但不是ws集群
        if (!serviceName.contains(getWsServiceName())) return;

        //获取哈希环
        Set<ZSetOperations.TypedTuple<Object>> hashSet = getOneHashRing2(ip, port, 5);

        //SortedMap<Object, Object> map = int2ObjMap(sortedMap);

        log.info("开始添加" + serviceName + "ip端口为:" + ip + ":" + port + "的哈希环");
        redisTemplate.opsForZSet().add(getHashRingKey(), hashSet);

        log.info("通知gateway有ws服务上线");
        //rabbitTemplate.convertAndSend("boot_topic_exchange", "boot.hashring", "1");

    }

    /**
     * 服务下线,或者不可用
     *
     * @param namespaceId :namespace
     * @param serviceName :service name
     * @param ip          :ip address
     * @param port        :port
     * @param healthy     :healthy flag
     * @param enabled     :enabled flag
     */
    public void updateHashRingByDeregister(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {

        System.out.println(serviceName + "请求服务下线, ip端口为: " + ip + ":" + port + "是否健康: " + (healthy ? "是" : "否") + "是否可用: " + (enabled ? "是" : "否"));

        if (serviceName.contains(getWsServiceName())) {

            SortedMap<Object, Object> sortedMap = getOneHashRing(ip, port, 5);

            Object[] keys = sortedMap.keySet().toArray();

            System.out.println("开始删除" + serviceName + "ip端口为:" + ip + ":" + port + "的哈希环");
            for (Object key : keys) {
                redisTemplate.opsForHash().delete(getHashRingKey(), key.toString());
            }

            System.out.println("通知gateway有ws服务下线");
            rabbitTemplate.convertAndSend("boot_topic_exchange", "boot.hashring", "0");
        }
    }

    /**
     * 健康状态改变
     *
     * @param namespaceId : Namespace
     * @param serviceName : Name of the service
     * @param ip          : IP address
     * @param port        : Port number
     * @param healthy     : boolean :
     * @param enabled     : boolean
     */
    public void updateHashRing(String namespaceId, String serviceName, String ip, int port, boolean healthy, boolean enabled) {
        if (healthy && enabled) {
            updateHashRingIsRegister(namespaceId, serviceName, ip, port, healthy, enabled);
        } else {
            updateHashRingByDeregister(namespaceId, serviceName, ip, port, healthy, enabled);
        }
    }
}
