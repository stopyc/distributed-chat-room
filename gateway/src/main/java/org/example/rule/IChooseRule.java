package org.example.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.server.ServerWebExchange;

/**
 * @Description 自定义选择实例规则
 * @Author Singh
 * @Date 2020-07-02 11:03
 * @Version
 **/
public interface IChooseRule {

    /**
     * 返回null那么使用gateway默认的负载均衡策略
     * @param exchange
     * @param discoveryClient
     * @return
     */
    ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient);

}
