package org.example.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author YC104
 */
@Component
public class UserConstants {

    /**
     * 超级管理员的用户id
     */
    public static Long SUPER_ADMIN_USER_ID;

    @Value("${setting.super-admin-user-id}")
    public void setIndustrialSoftwarePath(String userId) {
        SUPER_ADMIN_USER_ID = Long.parseLong(userId);
    }


}