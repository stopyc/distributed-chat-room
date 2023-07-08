package org.example.util;

import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWTUtil;
import org.example.pojo.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: monitor
 * @description: 封装jwtutils
 * @author: stop.yc
 * @create: 2023-04-15 16:18
 **/
public class JWTUtils {
    public static String parseJWT(String token) {
        JSONObject claimsJson = JWTUtil.parseToken(token).getPayload().getClaimsJson();
        Object exp = claimsJson.get("exp");

        LocalDateTime expireTime = TimeUtil.transfer(Long.parseLong(exp.toString()), LocalDateTime.class);

        if (expireTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("token已经过期了");
        }

        return claimsJson.toString();
    }
}
