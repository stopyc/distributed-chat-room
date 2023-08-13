package org.example.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.AtUserDTO;
import org.example.pojo.po.User;
import org.example.pojo.vo.ResultVO;
import org.example.pojo.vo.UserVO;

import java.util.List;


/**
 * @author YC104
 */
public interface UserService extends IService<User> {
    //ResultVO login(String username, String password, HttpServletResponse response);

    ResultVO logout();

    /**
     * 根据用户名获取
     *
     * @param username
     * @return
     */
    ResultVO getUserByUsername(String username, String ip);

    ResultVO testTc();

    ResultVO register(UserVO userVO);

    List<AtUserDTO> getUserListByIdList(List<Long> userIds);
}
