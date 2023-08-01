package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.factory.FrequencyControlStrategyFactory;
import org.example.pojo.dto.FrequencyControlDTO;
import org.example.pojo.dto.MessageDTO;
import org.example.websocket.GlobalWsMap;

import javax.annotation.PostConstruct;
import java.util.Map;


/**
 * 抽象类频控服务 其他类如果要实现限流服务 直接注入使用通用限流类
 * 后期会通过继承此类实现令牌桶等算法
 *
 * @author YC104
 */
@Slf4j
public abstract class AbstractFrequencyControlStrategy<K extends FrequencyControlDTO> {

    /**
     * bean初始化后注册自己到工厂
     */
    @PostConstruct
    protected void registerMyselfToFactory() {
        FrequencyControlStrategyFactory.registerFrequencyController(getStrategyName(), this);
    }

    /**
     * @param frequencyControlMap 定义的注解频控 Map中的Key-对应redis的单个频控的Key Map中的Value-对应redis的单个频控的Key限制的Value
     */
    public void executeWithFrequencyControlMap(Map<String, K> frequencyControlMap) {
        try {
            if (reachRateLimit(frequencyControlMap)) {
                frequencyControlMap.forEach((k, v) -> {
                    String[] split = k.split(":");
                    String userId = split[2];
                    MessageDTO messageDTO = MessageDTO.builder()
                            .messageType(3)
                            .message("发送消息不要超过频率喔")
                            .isText(true)
                            .build();
                    GlobalWsMap.sendText(Long.parseLong(userId), messageDTO);
                });
            }
        } finally {
            addFrequencyControlStatisticsCount(frequencyControlMap);
        }
    }

    /**
     * 是否达到限流阈值 子类实现 每个子类都可以自定义自己的限流逻辑判断
     *
     * @param frequencyControlMap 定义的注解频控 Map中的Key-对应redis的单个频控的Key Map中的Value-对应redis的单个频控的Key限制的Value
     * @return true-方法被限流 false-方法没有被限流
     */
    protected abstract boolean reachRateLimit(Map<String, K> frequencyControlMap);

    /**
     * 增加限流统计次数 子类实现 每个子类都可以自定义自己的限流统计信息增加的逻辑
     *
     * @param frequencyControlMap 定义的注解频控 Map中的Key-对应redis的单个频控的Key Map中的Value-对应redis的单个频控的Key限制的Value
     */
    protected abstract void addFrequencyControlStatisticsCount(Map<String, K> frequencyControlMap);

    /**
     * 获取策略名称
     *
     * @return 策略名称
     */
    protected abstract String getStrategyName();
}
