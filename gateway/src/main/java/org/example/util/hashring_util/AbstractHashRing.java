package org.example.util.hashring_util;

import com.alibaba.nacos.naming.utils.nacoshashring.entity.Address;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: util
 * @description: 抽象哈希环
 * @author: stop.yc
 * @create: 2023-07-06 15:47
 **/
@Component
public abstract class AbstractHashRing implements HashRing{

    /**
     * 默认虚拟结点数量
     */
    protected static final Integer DEFAULT_VIRTUAL_NODE_NUM = 5;

    /**
     * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
     */
    protected static SortedMap<Double, Address> virtualNodes = new TreeMap<>();

    protected Server server;

    protected static final String PREFIX_HASH_RING = "hashring:";

    @Override
    public void initHashRingFromCache() {
        getHashRingFromCache();
    }

    /**
     * 读写互斥锁
     */
    protected static final Object MUTEX = new Object();

    protected List<Address> getVirtualNodes(Address realNode, int virtualNum) {
        CopyOnWriteArrayList<Address> addresses = new CopyOnWriteArrayList<>();
        try {
            Address virtualAddress;
            for (int i = 0; i < virtualNum; i++) {
                virtualAddress = realNode.clone();
                virtualAddress.setVirtualNode(true);
                addresses.add(virtualAddress);
            }
        } catch (CloneNotSupportedException ignored) {}
        return addresses;
    }

    protected List<Address> getVirtualNodes(Address realNode) {
        return getVirtualNodes(realNode, DEFAULT_VIRTUAL_NODE_NUM);
    }

    /**
     * 从缓存中获取哈希环,看子类的缓存实现方式,项目采用redis.
     * @return: 哈希环
     */
    protected abstract SortedMap<Double, Address> getHashRingFromCache();

    /**
     * 从缓存中更新哈希环
     */
    @Override
    public synchronized void updateHashRingFromCache() {
        synchronized (MUTEX) {
            virtualNodes.clear();
        }
        getHashRingFromCache();
    }

    @Override
    public String getServerName() {
        return server.getServername();
    }

    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     * @param str :key Str
     * @return :hashCode
     */
    protected static int getHash(String str) {
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

    @Override
    public Address getAddress(String key) {
        synchronized (MUTEX) {
            if (virtualNodes.size() == 0) {
                return null;
            }
            //得到该key的hash值
            int hash = getHash(key);
            // 得到大于该Hash值的所有Map
            SortedMap<Double, Address> subMap = virtualNodes.tailMap((double)hash);
            Address virtualNode;
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
