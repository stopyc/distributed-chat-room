package org.example.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.vo.RequestInfo;
import org.example.util.RequestHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 请求拦截器,封装用户请求ip和用户id
 * @author YC104
 */
@Order(1)
@Slf4j
@Component
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestInfo info = new RequestInfo();
        info.setUid(Optional.ofNullable(request.getAttribute("uid")).map(Object::toString).map(Long::parseLong).orElse(null));
        info.setIp(ServletUtil.getClientIP(request));
        RequestHolder.set(info);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}