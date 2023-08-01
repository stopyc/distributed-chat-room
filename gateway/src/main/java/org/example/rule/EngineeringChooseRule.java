package org.example.rule;

import com.alibaba.nacos.naming.utils.nacoshashring.entity.Address;
import lombok.extern.slf4j.Slf4j;
import org.example.util.hashring_util.HashRing;
import org.example.util.hashring_util.Server;
import org.example.util.hashring_util.support.HashRingRedis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @Description  微服务负载均衡策略
 * @Author Singh
 * @Date 2020-07-02 11:10
 * @Version
 **/
@Slf4j
@Component
public class EngineeringChooseRule implements IChooseRule {

    @Resource
    @Qualifier("websocketServer")
    private Server server;


    @Override
    public ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient) {
        URI originalUrl = (URI) exchange.getAttributes().get(GATEWAY_REQUEST_URL_ATTR);
        String instancesId = originalUrl.getHost();

        if ("ws-service".equals(instancesId) && exchange.getRequest().getHeaders().containsKey("Sec-WebSocket-Key")) {

            List<ServiceInstance> instances = discoveryClient.getInstances(instancesId);

            RequestPath path = exchange.getRequest().getPath();
            String pathStr = path.toString();

            if (pathStr.contains("/ws")) {
                String[] split = pathStr.split("/");
                if (split.length == 7) {

                    String chatRoomId = split[2];

                    //String server = GateWayHashUtils.getServer(chatRoomId);

                    HashRing hashRingRedis = HashRingRedis.newInstance(server);
                    Address address = hashRingRedis.getAddress(chatRoomId);

                    log.info("ws服务站点数量为 为: {}", instances.size());
                    for (ServiceInstance instance : instances) {
                        //if (address != null && address.equals(instance.getHost() + ":" + instance.getPort())) {
                        if (address != null && address.getIp().equals(instance.getHost()) && address.getPort().equals(instance.getPort())) {
                            log.info("房间id为 {} 的用户请求的ws连接到的机器 ip:端口为 {}:{}", chatRoomId, instance.getHost(), instance.getPort());
                            return instance;
                        }
                    }
                }
            }
        }
        return null;
    }
}
