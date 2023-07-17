package org.example.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.vo.RequestInfo;
import org.example.util.RequestHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.Optional;

/**
 * 请求拦截器,封装用户请求ip和用户id
 *
 * @author YC104
 */
@Slf4j
@Component
public class CollectorFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestInfo info = new RequestInfo();
        info.setUid(Optional.ofNullable(request.getAttribute("uid")).map(Object::toString).map(Long::parseLong).orElse(null));
        String clientIP = request.getRemoteHost();
        info.setIp(clientIP);
        RequestHolder.set(info);
        chain.doFilter(request, response);
        RequestHolder.remove();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}