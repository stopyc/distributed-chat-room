package org.example.util;


import org.example.pojo.exception.BusinessException;
import org.springframework.stereotype.Component;

/**
 * @author YC104
 */
@Component
public class Assert {


    /**
     * 断言对象不为空
     *
     * @param obj: Object
     * @param msg: String
     */
    public static void assertNotNull(Object obj, String msg) {
        if (obj == null) {
            throw new BusinessException(msg);
        }
    }

    /**
     * 断言对象不为空
     *
     * @param obj: Object
     */
    public static void assertNotNull(Object obj) {
        if (obj == null) {
            throw new BusinessException("对象不能为空");
        }
    }

    /**
     * 断言字符串不为空
     *
     * @param str: String
     * @param msg: 报错提示信息
     */
    public static void assertNotEmpty(String str, String msg) {
        if (str == null || str.trim().length() == 0) {
            throw new BusinessException(msg);
        }
    }

    public static void assertNotEmpty(String str) {
        if (str == null || str.trim().length() == 0) {
            throw new BusinessException("字符串不能为空");
        }
    }

    public static void assertNotEmpty(String str, Executor executor) {
        if (str == null || str.trim().length() == 0) {
            executor.execute();
            throw new BusinessException("字符串不能为空");
        }
    }
}