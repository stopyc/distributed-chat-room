package org.example.constant;

import org.springframework.beans.factory.annotation.Value;

/**
 * @program: cloud
 * @description: 全局常量类
 * @author: stop.yc
 * @create: 2023-03-21 22:46
 **/
public class GlobalConstants {

    /**
     * 内部服务访问请求头 key
     */
    public static final String INTERNAL_CALL_REQUEST_HEADER_KEY = "x-service-call";

    /**
     * 内部服务访问请求头 value
     */
    public static final String INTERNAL_CALL_REQUEST_HEADER_VALUE = "inner";

    public static final String TRACE_ID = "traceid";

    public static final String REQUEST_ID = "requestid";

    public static final String SPAN_ID = "spanid";

    public static final String APPLICATION_NAME = "frontapplication";

    public static final String PROJECT_NAME = "projectname";

    public static final String DEFAULT_PROJECT_NAME = "default";






}
