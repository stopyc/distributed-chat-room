package org.example.util;

import com.alibaba.fastjson2.JSONObject;
import org.aspectj.weaver.ast.Var;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.UserAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.example.constant.ResultEnum.UNAUTHORIZED;


/**
 * @author YC104
 */
public class SecurityUtils
{

    /**
     * 获取用户账户
     **/
    public static String getUsername()
    {
        try
        {
            return getLoginUser().getUser().getUsername();
        }
        catch (Exception e)
        {
            throw new RuntimeException("获取用户账户异常");
        }
    }

    /**
     * 获取用户
     **/
    public static UserAuthority getLoginUser()
    {
        try
        {
            return (UserAuthority) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new AuthenticationException(UNAUTHORIZED.getMsg(),e){};
        }
    }
    public static UserBO getUser() {
        try {
            UserAuthority loginUser = getLoginUser();
            return JSONObject.parseObject(loginUser.getUser().getUsername(), UserBO.class);
        } catch (Exception e) {
            throw new AuthenticationException(UNAUTHORIZED.getMsg(),e){};
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}