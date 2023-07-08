package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.RoleMapper;
import org.example.pojo.po.Role;
import org.example.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @program: security-demo
 * @description: 用户业务层实现类
 * @author: stop.yc
 * @create: 2023-01-09 19:42
 **/
@Service
@Slf4j
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Resource
    private RoleMapper roleMapper;

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        return roleMapper.getRolesByUserId(userId);
    }
}
