package org.example.util.hashring_util;

import org.example.util.hashring_util.entity.Address;
import org.springframework.stereotype.Component;

/**
 * 哈希环方法接口
 * @author YC104
 */
@Component
public interface HashRing {

    /**
     * 获取服务名称
     * @return: 实现类下的服务名称
     */
    String getServerName();

    /**
     * 通过key获取哈希值,并映射到对应的哈希结点上
     * @param key: 需要被标识后进行哈希
     * @return :网络地址
     */
    Address getAddress(String key);

    /**
     * 更新本地缓存中的哈希环
     */
    void updateHashRingFromCache();

    /**
     * 初始化哈希环
     */
    void initHashRingFromCache();
}
