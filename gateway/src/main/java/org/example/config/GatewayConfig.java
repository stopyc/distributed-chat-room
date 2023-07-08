package org.example.config;

import org.example.filter.CustomLoadBalancerClientFilter;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.LoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

/**
 * @author YC104
 */
/**
 * @Description
 * @Author Singh
 * @Date 2020-07-01 17:57
 * @Version
 **/
@Configuration
public class GatewayConfig {


    @Bean
    public LoadBalancerClientFilter loadBalancerClientFilter(LoadBalancerClient client,
                                                             LoadBalancerProperties properties,
                                                             DiscoveryClient discoveryClient) {
        return new CustomLoadBalancerClientFilter(client, properties,discoveryClient);
    }

    @Bean
    public GlobalFilter customGlobalPostFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.just(exchange))
                .map(serverWebExchange -> {
                    //adds header to response
                    System.out.println("99999999999999999999");
                    serverWebExchange.getResponse().getHeaders().set("CUSTOM-RESPONSE-HEADER",
                            HttpStatus.OK.equals(serverWebExchange.getResponse().getStatusCode()) ? "It worked": "It did not work");
                    return serverWebExchange;
                })
                .then();
    }


}
