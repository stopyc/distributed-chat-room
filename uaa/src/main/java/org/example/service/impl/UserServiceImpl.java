package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.feign.UserClient;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.exception.FeignException;
import org.example.pojo.vo.ResultVO;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

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

        try {
            userByUsername = userClient.getByUsername(username);
        } catch (Exception e) {
            throw new FeignException(e.getMessage());
        }

        if (userByUsername.getCode() != REQUEST_SUCCESS.getCode()) {
            throw new FeignException(userByUsername.getMsg());
        }

        if (userByUsername.getData() == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        Object data = userByUsername.getData();

        UserAuthority userAuthority = BeanUtil.mapToBean((LinkedHashMap) data, UserAuthority.class, true);

        return userAuthority;
    }
}
