package org.example.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YC104
 */

@Getter
@AllArgsConstructor
public enum StrategyType {

    TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER("TotalCountWithInFixTime"),

    ;
    private final String strategyName;
}
