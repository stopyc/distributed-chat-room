package org.example.service.impl;

import org.example.pojo.dto.UserAuthority;
import org.example.util.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 自定义认证规则
 */
@Component("ss")
public class PermissionService
{
    /** 所有权限标识 */
    private static final String ALL_PERMISSION = "*:*:*";

    /** 管理员角色权限标识 */
    private static final String SUPER_ADMIN = "admin";


    /**
     * 权限分隔符
     */
    private static final String PERMISSION_DELIMETER = ",";

    /**
     * 验证用户是否具备某权限
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(String permission)
    {
        // 传进来的权限是否有效
        if (!StringUtils.hasText(permission))
        {
            return false;
        }
        if (permission.equals(ALL_PERMISSION)) {
            return true;
        }
        //获取认证用户
        UserAuthority loginUser = SecurityUtils.getLoginUser();

        //如果非认证用户,或者该用户没有权限
        if (Objects.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }

        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 验证用户是否不具备某权限，与 hasPermi逻辑相反
     *
     * @param permission 权限字符串
     * @return 用户是否不具备某权限
     */
    public boolean lacksPermi(String permission)
    {
        return !hasPermi(permission);
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以 PERMISSION_NAMES_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public boolean hasAnyPermi(String permissions)
    {
        if (!StringUtils.hasText(permissions))
        {
            return false;
        }

        UserAuthority loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions()))
        {
            return false;
        }
        List<String> authorities = loginUser.getPermissions();
        for (String permission : permissions.split(PERMISSION_DELIMETER))
        {

            if (permission != null && hasPermissions(authorities, permission))
            {
                return true;
            }

            if (StringUtils.hasText(permission) && permission.equals(ALL_PERMISSION)) {
                return true;
            }

        }
        return false;
    }


    /**
     * 判断是否包含权限
     * 
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(List<String> permissions, String permission)
    {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StringUtils.trimWhitespace(permission));
    }
}