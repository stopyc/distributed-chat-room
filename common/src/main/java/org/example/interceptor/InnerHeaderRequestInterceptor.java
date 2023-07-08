package org.example.interceptor;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.example.constant.GlobalConstants.*;

/**
 * @author YC104
 * @description: 内部服务, 全局拦截器, 对基于feign远程调用的请求, 添加上请求头
 */
public class InnerHeaderRequestInterceptor implements RequestInterceptor {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void apply(RequestTemplate template) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request2 = attributes.getRequest();
        //1.获取上游请求头参数并封装到openfeign接口调用中
        Enumeration<String> headerNames = request2.getHeaderNames();

        boolean flag1 = false;

        boolean flag2 = false;

        boolean flag3 = false;

        boolean flag4 = false;
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request2.getHeader(name);

                switch (name) {
                    case SPAN_ID:
                        template.removeHeader(SPAN_ID);
                        template.header(SPAN_ID, value + ".0");
                        flag1 = true;
                        break;
                    case TRACE_ID:
                        template.removeHeader(TRACE_ID);
                        template.header(TRACE_ID, Thread.currentThread().getId() + "");
                        flag2 = true;
                        break;
                    case INTERNAL_CALL_REQUEST_HEADER_KEY:
                        template.removeHeader(INTERNAL_CALL_REQUEST_HEADER_KEY);
                        template.header(INTERNAL_CALL_REQUEST_HEADER_KEY, INTERNAL_CALL_REQUEST_HEADER_VALUE);
                        flag3 = true;
                        break;
                    case APPLICATION_NAME:
                        template.removeHeader(APPLICATION_NAME);
                        template.header(APPLICATION_NAME, applicationName);
                        flag4 = true;
                        break;
                    default:
                        template.header(name, value);
                        break;
                }
            }
        }

        if (!flag1) {
            template.header(TRACE_ID, Thread.currentThread().getId() + "");
        }

        if (flag2) {
            template.header(SPAN_ID, "0");
        }
        if (!flag3) {
            template.header(INTERNAL_CALL_REQUEST_HEADER_KEY, INTERNAL_CALL_REQUEST_HEADER_VALUE);
        }

        if (!flag4) {
            template.header(APPLICATION_NAME, "gateway");
        }
    }
}