package org.example.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.example.feign.UserClient;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.ResultDTO;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @program: chat-room
 * @description: 用户存储层
 * @author: stop.yc
 * @create: 2023-07-30 18:14
 **/
@Component
@SuppressWarnings("all")
public class UserDao {

    private static UserClient userClient;

    static {
        UserDao.userClient = SpringUtil.getBean(UserClient.class);
    }


    public UserBO getUserBoByUserId(Long userId) {
        ResultDTO resultDTO = userClient.getById(userId);
        if (resultDTO.getCode() == 200) {
            return BeanUtil.mapToBean((LinkedHashMap) resultDTO.getData(), UserBO.class, true);
        }
        return null;
    }

    public Set<Long> getUserIdSetByChatRoomId(Long chatRoomId) {
        ResultDTO userSetByChatRoomId = userClient.getUserSetByChatRoomId(chatRoomId);
        HashSet<Long> hashset = new HashSet<>();
        if (userSetByChatRoomId.getCode() == 200) {
            List data = (ArrayList) userSetByChatRoomId.getData();
            for (Object datum : data) {
                hashset.add((long) ((int) datum));
            }
            return hashset;
        }
        return Collections.emptySet();
    }
}