package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.feign.ESClient;
import org.example.mapper.UserMapper;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.exception.SystemException;
import org.example.pojo.po.ChatRoom;
import org.example.pojo.po.Role;
import org.example.pojo.po.User;
import org.example.pojo.vo.ResultVO;
import org.example.pojo.vo.UserVO;
import org.example.service.IChatRoomService;
import org.example.service.MenuService;
import org.example.service.RoleService;
import org.example.service.UserService;
import org.example.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.example.constant.RedisKey.LOGOUT_KEY;
import static org.example.constant.ResultEnum.SUCCESS;
import static org.example.constant.ResultEnum.UNAUTHORIZED;
import static org.example.constant.RoleEnum.TOURIST;
import static org.example.constant.UserConstants.SUPER_ADMIN_USER_ID;


/**
 * @program: security-demo
 * @description: 用户业务层实现类
 * @author: stop.yc
 * @create: 2023-01-09 19:42
 **/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private ESClient esClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private IChatRoomService chatRoomService;

    @Resource
    private UserMapper userMapper;

    private static final String ANONYMOUS_USER = "anonymousUser";

    private static final String USER_DEFAULT_PASSWORD = "123456";


    @Override
    public ResultVO logout() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new BusinessException(UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal == null || ANONYMOUS_USER.equals(principal.toString())) {
            throw new BusinessException(UNAUTHORIZED);
        }
        Object jti = authentication.getCredentials();

        if (jti == null) {
            throw new SystemException("登出失败,token获取异常");
        }

        try {
            redisUtils.set(LOGOUT_KEY + jti.toString(), 1, 2L, TimeUnit.HOURS);
        } catch (Exception e) {
            log.info("redis重连");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            redisUtils.set(LOGOUT_KEY +  jti.toString(), 1, 2L, TimeUnit.HOURS);
        }

        //清空
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());

        return ResultVO.ok();
    }

    @Override
    public ResultVO getUserByUsername(String username, String ip) {

        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (Objects.isNull(user)) {
            ResultVO resultVO = register(UserVO.builder()
                    .username(username)
                    .password(USER_DEFAULT_PASSWORD)
                    .lastIp(ip)
                    .build());
            if (resultVO.getCode() != SUCCESS.getCode()) {
                throw new BusinessException(resultVO.getMsg());
            }
            user = lambdaQuery()
                    .eq(User::getUsername, username)
                    .one();
            chatRoomService.save(new ChatRoom(1L, user.getUserId()));
        }

        //权限集合
        Set<String> menus = new HashSet<>();

        if (user.getUserId().equals(SUPER_ADMIN_USER_ID)) {
            log.info("用户角色为超级管理员,拥有所有权限");
            menus.addAll(Collections.singleton("*:*:*"));
        } else {
            List<Role> roleList = roleService.getRolesByUserId(user.getUserId());

            if (CollectionUtils.isEmpty(roleList)) {
                log.info("用户的角色为空!");
            }

            log.info("获取用户角色,用户角色id为:{}", roleList);

            //获取用户所有角色对应的权限集合
            for (Role role : roleList) {
                menus.addAll(menuService.getMenuByRoleId(role.getRoleId()));
            }
            log.info("获取用户权限集合,权限集合为:{}", menus);
        }


        UserBO userBO = BeanUtil.copyProperties(user, UserBO.class);

        //包装用户dto
        UserDTO userDTO = UserDTO.builder()
                .username(JSONObject.toJSONString(userBO))
                .password(user.getPassword())
                .build();

        log.info("该用户的权限为: {} ",menus);

        return ResultVO.ok(
                UserAuthority.builder()
                        .user(userDTO)
                        .permissions(new ArrayList<>(menus))
                        .build()
        );
    }

    @Override
    public ResultVO testTc() {
        save(User.builder()
            .username("666")
                .password("123")
                .phone("13242597082")
        .build());

        esClient.r2();

        throw new BusinessException("testTc");

    }

    @Override
    public ResultVO register(UserVO userVO) {

        //1. 检验用户名是否重复
        String username = userVO.getUsername();
        int count = lambdaQuery()
                .eq(User::getUsername, username)
                .count();

        if (count > 0) {
            throw new BusinessException("用户名已经被注册了");
        }

        //3. 检验手机号是否重复
        String phone = userVO.getPhone();
        count = lambdaQuery()
                .eq(User::getPhone,phone)
                .count();
        if (count > 0) {
            throw new BusinessException("该手机号已经被注册了");
        }

        //4. 保存数据库
        String password = passwordEncoder.encode(userVO.getPassword());
        User user = BeanUtil.copyProperties(userVO, User.class);
        user.setPassword(password);
        save(user);

        userMapper.addRole2User(user.getUserId(), TOURIST.getRoleId());
        return ResultVO.ok();
    }
}
