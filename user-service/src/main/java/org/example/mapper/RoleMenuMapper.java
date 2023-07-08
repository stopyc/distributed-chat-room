package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author YC104
 */
@Mapper
public interface RoleMenuMapper {

    @Insert("insert into ${tableName} (`role_id`, `menu_id`) values (#{roleId}, #{menuId})")
    void addMenu2Role(Long menuId, Long roleId, String tableName);
}
