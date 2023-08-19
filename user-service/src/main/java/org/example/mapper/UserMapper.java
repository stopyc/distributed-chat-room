package org.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.pojo.AtUserDTO;
import org.example.pojo.po.User;

import java.util.List;

/**
 * @author YC104
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Insert("insert into t_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void addRole2User(Long userId, Long roleId);


    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "color", column = "color"),
            @Result(property = "icon", column = "icon")
    })
    @Select({
            "<script>",
            "select",
            "`user_id`, `username`, `icon`, `color`",
            "from t_user",
            "where `user_id` in",
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<AtUserDTO> getAtUserList(@Param("userIds") List<Long> userIds);
}
