package org.example.service;



import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.po.Menu;
import org.example.pojo.vo.MenuVO;
import org.example.pojo.vo.ResultVO;

import java.util.Set;


/**
 * @author YC104
 */
public interface MenuService extends IService<Menu> {


    Set<String> getMenuByRoleId(Long roleId);

    ResultVO getMenuList();

    ResultVO addMenu(MenuVO menuVO);

    ResultVO addMenu2Role(Long menuId, Long roleId);
}
