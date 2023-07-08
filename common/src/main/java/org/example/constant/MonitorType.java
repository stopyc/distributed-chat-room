package org.example.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 监控的类型: mysql、redis、jvm、服务器、接口参数、异常、sdk1、sdk2.。。
 * @author YC104
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MonitorType {

    /**
     * 服务链路监控
     */
    ServiceLink(0, "服务链路监控"),


    /**
     * 异常监控
     */
    Exception(1, "异常监控"),



    /**
     * 异常监控
     */
    Mysql(2, "Mysql监控"),


    /**
     * 异常监控
     */
    Redis(3, "Redis监控"),



    /**
     * 异常监控
     */
    JVM(4, "JVM监控"),


    /**
     * 异常监控
     */
    Server(5, "服务器监控"),






    ;

    private Integer type;

    private String typeName;

    public static MonitorType getTypeByCode(Integer type){
        Class<MonitorType> calzz = MonitorType.class;
        MonitorType[] enumConstants = calzz.getEnumConstants();
        for (MonitorType enumConstant : enumConstants) {
            if(enumConstant.getType().equals(type)){
                return enumConstant;
            }
        }
        return null;
    }


    public static MonitorType getTypeByMsg(String msg){
        Class<MonitorType> calzz = MonitorType.class;
        MonitorType[] enumConstants = calzz.getEnumConstants();
        for (MonitorType enumConstant : enumConstants) {
            if(enumConstant.getTypeName().equals(msg)){
                return enumConstant;
            }
        }
        return null;
    }
}
