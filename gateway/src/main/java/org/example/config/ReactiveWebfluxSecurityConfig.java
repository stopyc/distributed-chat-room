package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

/**
 * @program: cloud
 * @description: 全局关闭跨站攻击(不用动)
 * @author: stop.yc
 * @create: 2023-03-21 10:47
 **/
@EnableWebFluxSecurity
@Configuration
public class ReactiveWebfluxSecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
    ///**************解决网关转接websocket服务的问题*******************************/
    //@Bean
    //@Primary
    //WebSocketClient tomcatWebSocketClient() {
    //    return new TomcatWebSocketClient();
    //}
    //@Bean
    //@Primary
    //public RequestUpgradeStrategy requestUpgradeStrategy() {
    //    return new TomcatRequestUpgradeStrategy();
    //}
    ///**************解决网关转接websocket服务的问题*******************************/
}
