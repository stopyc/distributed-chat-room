package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.example.pojo.vo.ResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.example.constant.ResultEnum.TOKEN_FAILURE;

@Component
@Slf4j
public class MyExtendAuthenticationEntryPointHandler extends OAuth2AuthenticationEntryPoint {

	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        
        Throwable cause = authException.getCause();

        log.error("token异常: {}", authException.getMessage(), cause);

        //自定义返回格式内容
        ResultVO baseResult = ResultVO.fail(TOKEN_FAILURE);

        //System.out.println("cause.getClass().getName() = " + cause.getClass().getName());

        if (cause instanceof OAuth2AccessDeniedException) {
            baseResult.setMsg("资源ID不在resource_ids范围内");
        }  else if (cause instanceof InvalidTokenException) {
            if (authException.getMessage().contains("expired")) {
                baseResult.setMsg("Token已经过期");
            }else {
                baseResult.setMsg("Token解析失败");
            }
        }else if (authException instanceof InsufficientAuthenticationException) {
            baseResult.setMsg("未携带token");
        }else{
            baseResult.setMsg("未知异常信息");
        }

        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.append(new ObjectMapper().writeValueAsString(baseResult));

    }

}
