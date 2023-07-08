//package org.example.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
//import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.util.UriComponentsBuilder;
//import reactor.core.publisher.Mono;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.UUID;
//
///**
// * @program: chat-room
// * @description: ws一致性哈希过滤器
// * @author: stop.yc
// * @create: 2023-04-27 15:00
// **/
//
//@Component
//public class CustomerFilter implements GatewayFilter, Ordered {
//
//    @Override
//    public int getOrder() {
//        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        String newUrl = null;
//        String serviceName = "";
//
//        List<String> serviceHeaderList = exchange.getRequest().getHeaders().getValuesAsList("serviceName");
//
//        System.out.println("serviceHeaderList = " + serviceHeaderList);
//        if (serviceHeaderList.contains("s1")) {
//            newUrl = "http://127.0.0.1:9090/TestWebApp/testServlet";
//            serviceName = "s1";
//        } else if (serviceHeaderList.contains("s2")) {
//            newUrl = "http://127.0.0.1:9090/TestWebApp/regionServlet?flag=list";
//            serviceName = "s2";
//        }
//
//        try {
//            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, new URI(newUrl));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        Mono<Void> ret = chain.filter(exchange);
//
//        exchange.getResponse().getHeaders().add("doWork", serviceName + " ok");
//
//        return ret;
//    }
//}