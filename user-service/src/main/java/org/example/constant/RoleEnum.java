package org.example.constant;

import lombok.Getter;

/**
 * @author YC104
 */
@Getter
public enum RoleEnum {


    /**
     * 超级管理员
     */
    SUPER_ADMIN(1L, "超级管理员", "super_admin"),

    /**
     * 管理员
     */
    ADMIN(2L, "管理员", "admin"),

    /**
     * 普通用户
     */
    TOURIST(3L, "游客", "tourist"),








    ;

    private final Long roleId;

    private final String roleName;

    private final String roleKey;

    RoleEnum(Long roleId, String roleName, String roleKey) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleKey = roleKey;
    }
}
