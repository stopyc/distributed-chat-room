package org.example.filter;

import org.example.config.GatewayConfig;
import org.example.rule.EngineeringChooseRule;
import org.example.rule.IChooseRule;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestFactory;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @Description 自定义负载均衡
 * @Author Singh
 * @Date 2020-07-02 10:36
 * @Version
 **/
public class CustomLoadBalancerClientFilter extends LoadBalancerClientFilter implements BeanPostProcessor {

    private final DiscoveryClient discoveryClient;

    private final List<IChooseRule> chooseRules;

    public CustomLoadBalancerClientFilter(LoadBalancerClient loadBalancer,
                                          LoadBalancerProperties properties,
                                          DiscoveryClient discoveryClient) {
        super(loadBalancer, properties);
        this.discoveryClient = discoveryClient;
        this.chooseRules = new ArrayList<>();
        chooseRules.add(new EngineeringChooseRule());
    }

    @Override
    protected ServiceInstance choose(ServerWebExchange exchange) {
        if(!CollectionUtils.isEmpty(chooseRules)){
            for (IChooseRule chooseRule : chooseRules) {
                ServiceInstance choose = chooseRule.choose(exchange, discoveryClient);
                if (choose != null) {
                    return choose;
                }
            }
        }
        return loadBalancer.choose(
                ((URI) exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR)).getHost());
    }
}
