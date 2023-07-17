package org.example.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.util.JWTUtils;
import org.example.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.constant.RedisKey.LOGOUT_KEY;

/**
 * @program: cloud-demo
 * @description: 认证过滤器(不用动)
 * @author: stop.yc
 * @create: 2023-02-26 15:18
 **/
@Component
//@Order(0)
@Slf4j
public class AuthorizeFilter implements GlobalFilter , Ordered {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1. 获取请求头
        ServerHttpRequest request = exchange.getRequest();

        //2. 获取token
        String token = request.getHeaders().getFirst("Authorization");


        //2.1 如果为空,进行放行,因为后面还有资源拦截器
        if (!StringUtils.hasText(token)) {
            return chain.filter(exchange);
        }

        //2.2 如果不为空,则进行解析,并放入请求头中
        String value = null;
        try {
            //解析token
            String originalToken = token.replace("Bearer ", "");

            value = JWTUtils.parseJWT(originalToken);
            //Jwt jwt = JwtHelper.decode(originalToken);
            //
            ////获取token内容
            //value = jwt.getClaims();
        } catch (Exception e) {
            return Mono.error(new InvalidTokenException("无效的token!"));
        }

        //3. 转换为jsonObject便于取数据
        JSONObject jsonObject = JSONObject.parseObject(value);

        //4. 判断是否用户已经下线,但是token未过期
        // (jwt无状态,使用redis进行黑名单存储,对以下线的用户,存储jti,表示已经下线,然后过期时间为默认的两小时)
        Object jti = jsonObject.get("jti");

        String jtiStr= null;

        try {
            jtiStr = redisUtils.get(LOGOUT_KEY +  (String)jti, String.class);
        } catch (Exception e) {
            log.info("redis重连");
            try {
                Thread.sleep(100);
            }catch (InterruptedException ignored) {
            }
            jtiStr = redisUtils.get(LOGOUT_KEY +  (String)jti, String.class);
        }

        if (jtiStr != null) {
            return Mono.error(new InvalidTokenException("token expired 无效的token!"));
        }

        //token 有效

        //5. 原始参数进行无损传递
        Map<String, Object> jsonToken = new HashMap<>(jsonObject);

        //6. 新建权限和身份
        String authorities1 = jsonObject.getObject("authorities", String.class);
        List<String> authorities = JSONObject.parseArray(authorities1, String.class);

        //7. 如果是请求下线的话,需要把jti一起解析,传给后面服务群
        if ("/user/logout".equals(request.getURI().toString())) {
            jsonToken.put("jti", jti);
        }

        //"user_name"为security默认
        jsonToken.put("principal", jsonObject.get("user_name"));

        jsonToken.put("authorities", authorities);

        //8. 把token解析后的信息,放入jsonToken中,在微服务中传递
        request = request.mutate().header("jsonToken", Base64.encode(JSONObject.toJSONString(jsonToken))).build();


        return chain.filter(exchange.mutate().request(request).build());
    }



    @Override
    public int getOrder() {
        //order越小越先执行 当前Filter要在NettyWriteResponseFilter 之前运行
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}
