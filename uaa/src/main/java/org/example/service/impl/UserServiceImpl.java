package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.feign.UserClient;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.exception.FeignException;
import org.example.pojo.vo.ResultVO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.example.constant.ResultEnum.REQUEST_SUCCESS;


/**
 * @program: security-demo
 * @description: 用户业务层实现类
 * @author: stop.yc
 * @create: 2023-01-09 19:42
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {


    private static final String ANONYMOUS_USER = "anonymousUser";

    @Autowired
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("认证流程开始");

        ResultVO userByUsername = null;

        userByUsername = userClient.getByUsername(username);

        if (userByUsername.getCode() != REQUEST_SUCCESS.getCode()) {
            throw new FeignException(userByUsername.getMsg());
        }

        if (userByUsername.getData() == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        Object data = userByUsername.getData();

        System.out.println("data = " + data);

        if (data instanceof UserAuthority) {
            System.out.println(11);
        }

        Class<?> aClass = data.getClass();
        System.out.println(aClass.getName());

        UserAuthority userAuthority = BeanUtil.mapToBean((LinkedHashMap) data, UserAuthority.class, true);

        return userAuthority;
    }
}
