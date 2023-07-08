package org.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.po.User;

/**
 * @author YC104
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Insert("insert into t_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void addRole2User(Long userId, Long roleId);

}
