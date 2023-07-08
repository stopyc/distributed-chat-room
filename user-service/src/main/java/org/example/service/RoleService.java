package org.example.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.po.Role;

import java.util.List;

/**
 * @author YC104
 */
public interface RoleService extends IService<Role> {


    List<Role> getRolesByUserId(Long userId);
}
