//package org.example.listener;
//
//import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
//import com.alibaba.nacos.api.NacosFactory;
//import com.alibaba.nacos.api.annotation.NacosInjected;
//import com.alibaba.nacos.api.annotation.NacosProperties;
//import com.alibaba.nacos.api.config.ConfigService;
//import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
//import com.alibaba.nacos.api.config.listener.AbstractListener;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.NamingFactory;
//import com.alibaba.nacos.api.naming.NamingService;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import io.micrometer.core.instrument.util.JsonUtils;
//import jdk.nashorn.internal.ir.annotations.Reference;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.exception.ExceptionUtils;
//import org.example.util.HashUtils;
//import org.example.util.RedisUtils;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.yaml.snakeyaml.Yaml;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.*;
//
//import static org.example.constant.RedisKey.PREFIX_HASH_RING;
//
///**
// * @author YC104
// */
//@Configuration
//@Slf4j
//@ConditionalOnNacosDiscoveryEnabled
//@Reference
//public class NacosConfig implements InitializingBean, DisposableBean {
//
//    private String dataId = "application-dev.yaml";
//
//    @Value("${spring.cloud.nacos.server-addr}")
//    private String serverAddr;
//
//    @Resource
//    private RedisUtils redisUtils;
//
//    private String groupName = "DEFAULT_GROUP";
//
//    private String namespace;
//
//    private static final String LISTEN_WS_SERVICE_NAME = "ws-service";
//
//    /**
//     * 服务注册功能的Service
//     */
//    @NacosInjected
//    private NamingService namingService;
//
//    private ConfigService configService;
//
//    public NacosConfig() throws NacosException {
//    }
//
//    @PostConstruct
//    public void init() {
//        //log.info("nacos获取ws服务列表，开始初始化哈希环...");
//        //
//        //NamingService namingService = null;
//        //try {
//        //    namingService = NamingFactory.createNamingService(serverAddr);
//        //
//        //    //1. 初始化ws服务哈希环
//        //    List<Instance> allInstances = null;
//        //
//        //    // 更新哈希环,如果不存在则创建
//        //    updateHashRing(namingService);
//        //
//        //    log.info("nacos创建哈希环成功, 已存入redis中");
//        //
//        //} catch (NacosException e) {
//        //    e.printStackTrace();
//        //}
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        configService.shutDown();
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//
//
//        ////启动配置文件时监听
//        //Properties properties = new Properties();
//        //properties.put("serverAddr", serverAddr);
//        //
//        //ConfigService configService = NacosFactory.createConfigService(properties);
//        //configService = NacosFactory.createConfigService(properties);
//        //
//        //try {
//        //    //修改配置文件时监听
//        //    configService.addListener(dataId, group, new AbstractListener() {
//        //        @Override
//        //        public void receiveConfigInfo(String configInfo) {
//        //            Yaml yaml = new Yaml();
//        //            Map load = yaml.load(configInfo);
//        //            log.info("监听到最新的配置文件信息：{}", JsonUtils.obj2Json(load));
//        //            //获取配置文件中的某一项值
//        //            Integer choose = (Integer) load.get("choose");
//        //        }
//        //    });
//        //} catch (NacosException e) {
//        //    log.error(ExceptionUtils.getStackTrace(e));
//        //}
//
//        try {
//            //监听ws服务（启动，修改，关闭都会监听到）
//            NamingService namingService = NamingFactory.createNamingService(serverAddr);
//
//            namingService.subscribe(LISTEN_WS_SERVICE_NAME, groupName, event -> {
//
//                log.info("nacos监听到ws服务状态变更!");
//
//                log.info("nacos开始创建或更新哈希环...");
//
//                // 更新哈希环
//                updateHashRing(namingService);
//
//                log.info("nacos更新哈希环成功, 已存入redis");
//            });
//
//        } catch (NacosException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    private void updateHashRing(NamingService namingService) {
//        List<Instance> allInstances;
//        try {
//            //2. 获取所有健康的ws服务的列表
//            allInstances = namingService.selectInstances(LISTEN_WS_SERVICE_NAME, true);
//
//            ////3. 移出未存活的
//            //allInstances.removeIf(instance -> !instance.isHealthy());
//
//            //4. 根据服务列表获取哈希环
//            SortedMap<Integer, String> hashRing = HashUtils.getHashRing(allInstances, 5);
//
//            //5. 存入redis中
//            redisUtils.setMap(PREFIX_HASH_RING, hashRing);
//
//        } catch (NacosException e) {
//            e.printStackTrace();
//        }
//    }
//}