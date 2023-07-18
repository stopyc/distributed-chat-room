package org.example.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWTUtil;
import org.example.pojo.bo.UserBO;
import org.example.pojo.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.example.constant.RedisKey.LOGOUT_KEY;

/**
 * @program: monitor
 * @description: 封装jwtutils
 * @author: stop.yc
 * @create: 2023-04-15 16:18
 **/
@Component
public class JWTUtils {

    private static RedisUtils redisUtils;

    static {
        JWTUtils.redisUtils = SpringUtil.getBean(RedisUtils.class);
    }

    public static String parseJWT(String token) {
        try {

            JSONObject claimsJson = JWTUtil.parseToken(token).getPayload().getClaimsJson();
            Object exp = claimsJson.get("exp");
            LocalDateTime expireTime = TimeUtil.transfer(Long.parseLong(exp.toString()), LocalDateTime.class);
            if (expireTime.isBefore(LocalDateTime.now())) {
                throw new BusinessException("token已经过期了");
            }

            return claimsJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("token解析失败");
        }
    }

    public static UserBO parseJWT2UserBo(String token) throws BusinessException {
        //2.1 解析token
        String value = parseJWT(token);
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(value);

        //4. 判断是否用户已经下线,但是token未过期
        // (jwt无状态,使用redis进行黑名单存储,对以下线的用户,存储jti,表示已经下线,然后过期时间为默认的两小时)
        Object jti = jsonObject.get("jti");

        String jtiStr = null;

        jtiStr = redisUtils.get(LOGOUT_KEY + (String) jti, String.class);

        if (jtiStr != null) {
            throw new BusinessException("token已经无效");
        }
        Object userObj = jsonObject.get("user_name");
        return com.alibaba.fastjson.JSONObject.parseObject(userObj.toString(), UserBO.class);
    }
}
