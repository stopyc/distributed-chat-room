package org.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.pojo.po.Menu;

import java.util.List;

/**
 * @author YC104
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {


    @Select("select `menu_key` " +
            "   from t_menu m " +
            "       left join t_role_menu rm on m.menu_id = rm.menu_id" +
            " where rm.role_id = #{roleId}")
    List<String> getMenuByRoleId(Long roleId);
}
