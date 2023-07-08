package org.example.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import feign.HeaderMap;
import org.apache.catalina.connector.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.tomcat.util.http.MimeHeaders;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.dto.UserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.constant.GlobalConstants.*;

/**
 * @program: cloud
 * @description: 全局token获取解析,并放入安全上下文
 * @author: stop.yc
 * @create: 2023-03-20 22:58
 **/
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {



    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse
            httpServletResponse, FilterChain filterChain) throws ServletException, IOException {


        //HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);


        //1. 获取以base64加密的jsontoken
        String token = httpServletRequest.getHeader("jsonToken");

        //2. 如果不为null,需要进行解析,并放入安全上下文
        if (token != null) {
            //2.1 解析token
            String json = Base64.decodeStr(token);
            JSONObject userJson = JSON.parseObject(json);

            //2.2 获取jsonToken中的用户角色
            String user = (String) userJson.get("principal");

            //2.3 权限信息
            JSONArray authoritiesArray = userJson.getJSONArray("authorities");

            //2.4 转为数组
            String[] authorities = authoritiesArray.toArray(new String[0]);

            UserDTO userObj = null;

            //2.5 解析为用户对象
            if (StringUtils.hasText(user)) {
                userObj = JSONObject.parseObject(user, UserDTO.class);
                userObj.setUsername(user);
            }

            List<String> strings = Arrays.asList(authorities);

            UserAuthority userAuthority = new UserAuthority(userObj, strings);

            //2.6 新建并填充authentication(用户,null,权限)
            UsernamePasswordAuthenticationToken authentication = new
                    UsernamePasswordAuthenticationToken(
                    //用户信息
                    new UserAuthority(userObj, Arrays.asList(authorities)),
                    //jti信息
                    Optional.ofNullable(userJson.get("jti")).orElse(null),
                    //权限信息
                    AuthorityUtils.createAuthorityList(authorities));

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            //2.7 将authentication保存进安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //3. 如果不带token,可以直接放行,因为还有权限拦截
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void reflectSetparam(HttpServletRequest request,String key,String value)  {
        try {
            // 从 RequestFacade 中获取 org.apache.catalina.connector.Request
            Field connectorField = ReflectionUtils.findField(RequestFacade.class, "request", Request.class);
            connectorField.setAccessible(true);
            Request connectorRequest = (Request) connectorField.get(request);

            // 从 org.apache.catalina.connector.Request 中获取 org.apache.coyote.Request
            Field coyoteField = ReflectionUtils.findField(Request.class, "coyoteRequest", org.apache.coyote.Request.class);
            coyoteField.setAccessible(true);
            org.apache.coyote.Request coyoteRequest = (org.apache.coyote.Request) coyoteField.get(connectorRequest);

            // 从 org.apache.coyote.Request 中获取 MimeHeaders
            Field mimeHeadersField =  ReflectionUtils.findField(org.apache.coyote.Request.class, "headers", MimeHeaders.class);
            mimeHeadersField.setAccessible(true);
            MimeHeaders mimeHeaders =  (MimeHeaders) mimeHeadersField.get(coyoteRequest);

            this.mineHeadersHandle(mimeHeaders, key,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    protected void mineHeadersHandle (MimeHeaders mimeHeaders, String key, String value) {
        // 添加一个Header，随机生成请求ID
        mimeHeaders.addValue(key).setString(value);;
    }
}

