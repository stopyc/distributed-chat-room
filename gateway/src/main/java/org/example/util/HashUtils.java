//package org.example.util;
//
//import cn.hutool.core.bean.BeanUtil;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.NamingFactory;
//import com.alibaba.nacos.api.naming.NamingService;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.validation.constraints.NotNull;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.*;
//
//import static org.example.constant.RedisKey.PREFIX_HASH_RING;
//
///**
// * @program: chat-room
// * @description: 一致性哈希算法实现类
// * @author: stop.yc
// * @create: 2023-04-30 16:53
// **/
//@Slf4j
//@Component
//public class HashUtils {
//
//    @Resource
//    private RedisUtils redisUtils;
//
//    //虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
//    private static final SortedMap<Integer, String> virtualNodes = new TreeMap<>();
//
//
//    public void getHashRingFromRedis() {
//        log.info("gateway获取redis中的哈希环!");
//        // 启动时,获取哈希环,供gateway使用
//
//        Map<Object, Object> map = redisUtils.getMap(PREFIX_HASH_RING);
//
//        if (CollectionUtils.isEmpty(map)) {
//            log.info("redis中哈希环为空, 当前无ws服务启动或nacos未启动开始初始化!");
//            return;
//        }
//
//        log.info("gateway获取redis中的哈希环中每个ws节点位置为:");
//        for (Map.Entry<Object, Object> entry : map.entrySet()) {
//
//            String hash = entry.getKey().toString();
//            String ipPort = entry.getValue().toString();
//
//            log.info("hash ({}) --> ip:port ({})", hash, ipPort);
//
//            virtualNodes.put(Integer.parseInt(hash), ipPort);
//        }
//
//        log.info("gateway中存放的哈希环的键值对为");
//        for (Map.Entry<Integer, String> entry : virtualNodes.entrySet()) {
//            log.info("entry的键为{} 值为: {}",entry.getKey(), entry.getValue());
//        }
//    }
//
//    /**
//     * 更新哈希环
//     */
//    public void updateHashRing() {
//        // 启动时,获取哈希环,供gateway使用
//        Map<Object, Object> map = redisUtils.getMap(PREFIX_HASH_RING);
//
//        if (CollectionUtils.isEmpty(map)) {
//            log.info("redis中哈希环为空, 当前无ws服务启动或nacos未启动开始初始化!");
//            return;
//        }
//        // 清空哈希环
//        virtualNodes.clear();
//
//        log.info("gateway获取redis中的哈希环中每个ws节点位置为:");
//        for (Map.Entry<Object, Object> entry : map.entrySet()) {
//
//            String hash = entry.getKey().toString();
//            String ipPort = entry.getValue().toString();
//
//            log.info("hash ({}) --> ip:port ({})", hash, ipPort);
//
//            virtualNodes.put(Integer.parseInt(hash), ipPort);
//        }
//    }
//
//    /**
//     * 根据服务真实结点,虚拟结点数量,获取哈希环
//     *
//     * @param realNodes         :真实服务集合
//     * @param virtualNodesCount :虚拟结点数量(0为不需要虚拟结点,-1为默认的3个虚拟结点)
//     * @return :哈希环(key: hash值; value: ip:端口)
//     */
//    public static SortedMap<Integer, String> getHashRing(@NotNull List<Instance> realNodes, int virtualNodesCount) {
//
//        if (virtualNodesCount == -1) {
//            virtualNodesCount = 3;
//        }
//
//        SortedMap<Integer, String> virtualNodes = new TreeMap<>();
//
//        //1. 遍历所有真实结点
//        for (Instance realNode : realNodes) {
//            //2. 获取真实结点的名称
//            String realNodeName = realNode.getIp() + ":" + realNode.getPort();
//
//            //3. 根据名称获取hash
//            int realNodeHash = getHash(realNodeName);
//
//            //4. 添加真实结点
//            log.info("ip端口为 {}:{}的ws服务的真实结点的hash为:{}", realNode.getIp(), realNode.getPort(), realNodeHash);
//
//            virtualNodes.put(realNodeHash, realNodeName);
//
//            //5. 对真实结点添加虚拟结点
//            for (int i = 0; i < virtualNodesCount; i++) {
//
//                //5.1 获取虚拟结点的结点名称
//                String virtualNodeName = realNodeName + "&&VN" + i;
//
//                //5.2 根据名称进行hash
//                int hash = getHash(virtualNodeName);
//                log.info("ip端口为 {}:{}的ws服务的第{}个虚拟结点的hash为:{}", realNode.getIp(), realNode.getPort(), i + 1, hash);
//
//                //5.3 添加虚拟结点(hash值不一样,但是value为ip:端口,和真实结点一致)
//                virtualNodes.put(hash, realNodeName);
//            }
//        }
//
//        return virtualNodes;
//    }
//
//
//    //使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
//    private static int getHash(String str) {
//        final int p = 16777619;
//        int hash = (int) 2166136261L;
//        for (int i = 0; i < str.length(); i++) {
//            hash = (hash ^ str.charAt(i)) * p;
//        }
//        hash += hash << 13;
//        hash ^= hash >> 7;
//        hash += hash << 3;
//        hash ^= hash >> 17;
//        hash += hash << 5;
//
//        // 如果算出来的值为负数则取其绝对值
//        if (hash < 0) {
//            hash = Math.abs(hash);
//        }
//        return hash;
//    }
//
//    //得到应当路由到的结点
//    public static String getServer(String key) {
//
//        if (virtualNodes .size() == 0) {
//            return null;
//        }
//        //得到该key的hash值
//        int hash = getHash(key);
//        // 得到大于该Hash值的所有Map
//        SortedMap<Integer, String> subMap = virtualNodes.tailMap(hash);
//        String virtualNode;
//        if (subMap.isEmpty()) {
//            //如果没有比该key的hash值大的，则从第一个node开始
//            Integer i = virtualNodes.firstKey();
//            //返回对应的服务器
//            virtualNode = virtualNodes.get(i);
//        } else {
//            //第一个Key就是顺时针过去离node最近的那个结点
//            Integer i = subMap.firstKey();
//            //返回对应的服务器
//            virtualNode = subMap.get(i);
//        }
//
//        return virtualNode;
//    }
//
//}
