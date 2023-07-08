package org.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.pojo.po.Role;

import java.util.List;

/**
 * @author YC104
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {


    /**
     * 通过用户id查询角色集合
     * @param userId :用户
     * @return :用户的角色集合
     */
    @Select("select * " +
            "from `t_role` r " +
            "   left join `t_user_role` ur on r.role_id = ur.role_id " +
            "where ur.user_id = #{userId}")
    List<Role> getRolesByUserId(Long userId);
}
