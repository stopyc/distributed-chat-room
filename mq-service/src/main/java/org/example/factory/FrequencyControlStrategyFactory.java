package org.example.factory;


import org.example.pojo.dto.FrequencyControlDTO;
import org.example.strategy.AbstractFrequencyControlStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 把策略子类初始化进工厂
 *
 * @author YC104
 */
public class FrequencyControlStrategyFactory {

    /**
     * 限流策略集合
     */
    static Map<String, AbstractFrequencyControlStrategy<?>> frequencyControlServiceStrategyMap = new ConcurrentHashMap<>(8);

    /**
     * 构造器私有
     */
    private FrequencyControlStrategyFactory() {

    }

    /**
     * 将策略类放入工厂
     *
     * @param strategyName                     策略名称
     * @param abstractFrequencyControlStrategy 策略类
     */
    public static <K extends FrequencyControlDTO> void registerFrequencyController(String strategyName, AbstractFrequencyControlStrategy<K> abstractFrequencyControlStrategy) {
        frequencyControlServiceStrategyMap.put(strategyName, abstractFrequencyControlStrategy);
    }

    /**
     * 根据名称获取策略类
     *
     * @param strategyName 策略名称
     * @return 对应的限流策略类
     */
    @SuppressWarnings("unchecked")
    public static <K extends FrequencyControlDTO> AbstractFrequencyControlStrategy<K> getFrequencyControllerByName(String strategyName) {
        return (AbstractFrequencyControlStrategy<K>) frequencyControlServiceStrategyMap.get(strategyName);
    }
}