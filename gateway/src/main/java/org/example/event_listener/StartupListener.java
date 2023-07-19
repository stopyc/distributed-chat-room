package org.example.event_listener;

import org.example.util.GateWayHashUtils;
import org.example.util.hashring_util.HashRing;
import org.example.util.hashring_util.Server;
import org.example.util.hashring_util.support.HashRingRedis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author YC104
 */
@Component
public class StartupListener implements ApplicationRunner {

    @Resource
    private GateWayHashUtils hashUtils;
    @Resource
    @Qualifier("websocketServer")
    private Server server;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化哈希环
        //hashUtils.getHashRingFromRedis();
        HashRing hashRingRedis = HashRingRedis.newInstance(server);
        hashRingRedis.initHashRingFromCache();
    }
}
