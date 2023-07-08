package org.example.util;

import cn.hutool.core.bean.BeanUtil;
import org.example.pojo.exception.BusinessException;

import java.util.Map;

/**
 * @program: chat-room
 * @description: 我的自定义beanUtil
 * @author: stop.yc
 * @create: 2023-04-29 11:23
 **/
public class BeanUtils {

    public static <T> T objMapToBean(Object objMap, Class<T> beanType) {
        if (objMap instanceof Map) {
            Map<?, ?> mapValue = (Map<?, ?>)objMap;
            return BeanUtil.mapToBean(mapValue, beanType, false);
        }else {
            throw new BusinessException("数据不为map类型");
        }
    }
}
