package org.example.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.vo.ResultVO;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.example.constant.ResultEnum.CLIENT_AUTHENTICATION_ERROR;

/**
 * @program: cloud
 * @description: 自定义认证异常
 * @author: stop.yc
 * @create: 2023-03-22 16:21
 **/
@Component
@Slf4j
public class OAuthServerAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        log.error("客户端认证失败",e);
        httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultVO.fail(CLIENT_AUTHENTICATION_ERROR)));
    }
}
