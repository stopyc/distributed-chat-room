package org.example.util;

import org.example.pojo.vo.RequestInfo;

/**
 * 全局获取用户id和ip的工具
 * @author YC104
 */
public class RequestHolder {

    private static final ThreadLocal<RequestInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo) {
        THREAD_LOCAL.set(requestInfo);
    }

    public static RequestInfo get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
