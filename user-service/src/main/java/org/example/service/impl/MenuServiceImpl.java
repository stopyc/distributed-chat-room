package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.MenuMapper;
import org.example.mapper.RoleMenuMapper;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.po.Menu;
import org.example.pojo.vo.MenuTreeVO;
import org.example.pojo.vo.MenuVO;
import org.example.pojo.vo.ResultVO;
import org.example.service.MenuService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: security-demo
 * @description: 用户业务层实现类
 * @author: stop.yc
 * @create: 2023-01-09 19:42
 **/
@Service
@Slf4j
//统一配置缓存前缀名称
@CacheConfig(cacheNames = {"menu"})
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;


    private static final String TABLE_NAME = "t_role_menu";
    private static final String PERMISSION_FORMAT2 = "\\w+:\\w+";
    private static final String PERMISSION_FORMAT3 = "\\w+";

    @Override
    @Cacheable(key = "'role:' + #p0")
    public Set<String> getMenuByRoleId(Long roleId) {

        if (roleId == null) {
            return Collections.emptySet();
        }

        List<String> menus = menuMapper.getMenuByRoleId(roleId);

        Set<String> menuList = menus.stream()
                .map(menu -> {
                    if (menu.matches(PERMISSION_FORMAT2)) {
                        return menu + ":*";
                    } else if (menu.matches(PERMISSION_FORMAT3)) {
                        return menu + ":*:*";
                    } else {
                        return menu;
                    }
                }).collect(Collectors.toSet());
        return menuList;
    }

    @Override
    @Cacheable(key = "'list'")
    public ResultVO getMenuList() {
        // 1. 查询所有的权限
        List<Menu> menuList = list();
        List<MenuTreeVO> menuTreeVOList = new ArrayList<>();

        // 2.找出顶级结点
        for (Menu menu : menuList) {
            if (menu.getParentId() == 0) {

                //2.1建立vo对象,创建子集合
                MenuTreeVO parent = MenuTreeVO.builder().childList(new ArrayList<>()).build();
                BeanUtil.copyProperties(menu, parent);

                //2.2 添加进结果集
                menuTreeVOList.add(parent);

                //2.3. 寻找父级结点的子节点
                findChildren(menu, parent, menuList);
            }
        }


        return ResultVO.ok(menuTreeVOList);
    }

    /**
     * 添加权限清单
     *
     * @param menuVO :权限vo对象
     * @return :成功与否
     */
    @Override
    @CacheEvict(key = "'list'")
    public ResultVO addMenu(MenuVO menuVO) {
        Menu menu = BeanUtil.copyProperties(menuVO, Menu.class);
        //1.判断是否是顶级权限

        //1.1.如果是,直接数据库添加即可
        if (menuVO.getParentId() == null || menuVO.getParentId() == 0) {
            menu.setParentId(0L);

            //1.2.如果不是,需要查询上级的权限key,然后拼接形成该权限的key,再添加进数据库
        } else {
            StringBuilder menuKey = new StringBuilder();

            //1.2.1.查询上级的权限key
            findParentKey(menu, menuKey);

            //1.2.2 如果拼接失败,表示父级id错误,查询失败
            if (!StringUtils.hasText(menuKey.toString())) {
                throw new BusinessException("传递的父级id错误");
            }

            menu.setMenuKey(menuKey.append(menu.getMenuKey()).toString());
        }
        save(menu);

        return ResultVO.ok();
    }

    @Override
    @CacheEvict(key = "'role:' + #p1")
    public ResultVO addMenu2Role(Long menuId, Long roleId) {
        roleMenuMapper.addMenu2Role(menuId, roleId, TABLE_NAME);
        return ResultVO.ok();
    }

    /**
     * 创建menuKey
     *
     * @param menu    :menuVo
     * @param menuKey :menuKey
     */
    private void findParentKey(Menu menu, StringBuilder menuKey) {
        Menu parentMenu = lambdaQuery()
                .eq(Menu::getMenuId, menu.getParentId())
                .one();
        if (parentMenu != null) {
            menuKey.append(parentMenu.getMenuKey()).append(":");
        }
    }

    /**
     * 寻找父级结点的子节点
     *
     * @param parentMenu :父级结点
     * @param parentVO   :需要返回前端的父级结点,里面填充了子节点集合
     * @param menuList   :所有权限集合
     */
    private void findChildren(Menu parentMenu, MenuTreeVO parentVO, List<Menu> menuList) {
        for (Menu menu : menuList) {
            //如果是父级结点的子节点
            if (menu.getParentId().equals(parentMenu.getMenuId())) {
                MenuTreeVO parent = MenuTreeVO.builder().childList(new ArrayList<>()).build();
                BeanUtil.copyProperties(menu, parent);
                parentVO.getChildList().add(parent);

                //继续寻找子节点
                findChildren(menu, parent, menuList);
            }
        }
    }
}
