package org.example.aspect;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.annotation.FrequencyControl;
import org.example.factory.FrequencyControlStrategyFactory;
import org.example.pojo.dto.FrequencyControlDTO;
import org.example.pojo.exception.FrequencyControlException;
import org.example.pojo.vo.ResultVO;
import org.example.utils.SpElUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.strategy.StrategyType.TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER;


/**
 * Description: 频控实现
 * Date: 2023-07-12
 *
 * @author YC104
 */
@Slf4j
@Aspect
@Component
public class FrequencyControlAspect {

    @Around("@annotation(org.example.annotation.FrequencyControl)||@annotation(org.example.annotation.FrequencyControlContainer)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        FrequencyControl[] annotationsByType = method.getAnnotationsByType(FrequencyControl.class);
        Map<String, FrequencyControl> keyMap = new HashMap<>(8);
        String strategyName = null;
        for (int i = 0; i < annotationsByType.length; i++) {
            FrequencyControl frequencyControl = annotationsByType[i];
            //默认方法限定名+注解排名（可能多个）
            String prefix = (StrUtil.isBlank(frequencyControl.prefixKey()) ? SpElUtils.getMethodKey(method) + ":index:" + i : frequencyControl.prefixKey());
            String key = "";
            switch (frequencyControl.target()) {
                case EL:
                    key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), frequencyControl.spEl());
                    strategyName = TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER.getStrategyName();
                    break;
                case IP:
                    key = "127.0.0.1";
                    strategyName = TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER.getStrategyName();
                    break;
                case UID:
                    key = "1";
                    strategyName = TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER.getStrategyName();
                    break;
            }
            keyMap.put(prefix + ":" + key, frequencyControl);

        }
        Map<String, FrequencyControlDTO> frequencyControlDtoMap = keyMap
                .entrySet()
                .stream()
                .map(entrySet -> buildFrequencyControlDTO(entrySet.getKey(), entrySet.getValue()))
                .collect(Collectors.toMap(FrequencyControlDTO::getKey, Function.identity()));

        try {
            FrequencyControlStrategyFactory
                    .getFrequencyControllerByName(strategyName)
                    .executeWithFrequencyControlMap(frequencyControlDtoMap);
        } catch (FrequencyControlException e) {
            log.error(" 接口 {} 频率控制异常", method.getName(), e);
            //实现降级处理
            return ResultVO.ok("降级处理");
        }

        return joinPoint.proceed();
    }

    /**
     * 将注解参数转换为编程式调用所需要的参数
     *
     * @param key              频率控制Key
     * @param frequencyControl 注解
     * @return 编程式调用所需要的参数-FrequencyControlDTO
     */
    private FrequencyControlDTO buildFrequencyControlDTO(String key, FrequencyControl frequencyControl) {
        FrequencyControlDTO frequencyControlDTO = new FrequencyControlDTO();
        frequencyControlDTO.setCount(frequencyControl.count());
        frequencyControlDTO.setTime(frequencyControl.time());
        frequencyControlDTO.setUnit(frequencyControl.unit());
        frequencyControlDTO.setKey(key);
        return frequencyControlDTO;
    }
}
