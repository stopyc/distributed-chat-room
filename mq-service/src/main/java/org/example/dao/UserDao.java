package org.example.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import org.example.constant.RedisKey;
import org.example.feign.UserClient;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.ResultDTO;
import org.example.util.RedisNewUtil;
import org.example.websocket.GlobalWsMap;
import org.example.websocket.MyWebSocket;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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


    @Cacheable(value = "user", key = "#userId")
    public UserBO getUserBoByUserId(Long userId) {
        ResultDTO resultDTO = null;
        try {
            resultDTO = userClient.getById(userId);
            if (resultDTO.getCode() == 200) {
                return BeanUtil.mapToBean((LinkedHashMap) resultDTO.getData(), UserBO.class, true);
            }
        } catch (Exception e) {
            return new UserBO();
        }
        return new UserBO();
    }

    @Cacheable(value = "chatroom", key = "#chatRoomId")
    public Set<Long> getUserIdSetByChatRoomId(Long chatRoomId) {
        ResultDTO userSetByChatRoomId = null;
        try {
            userSetByChatRoomId = userClient.getUserSetByChatRoomId(chatRoomId);
            HashSet<Long> hashset = new HashSet<>();
            if (userSetByChatRoomId.getCode() == 200) {
                List data = (ArrayList) userSetByChatRoomId.getData();
                for (Object datum : data) {
                    hashset.add((long) ((int) datum));
                }
                return hashset;
            }
        } catch (Exception e) {
            return Collections.emptySet();
        }
        return Collections.emptySet();
    }

    public void addCacheUser(MyWebSocket myWebSocket) {
        RedisNewUtil.mput(RedisKey.USER_ONLINE, "", myWebSocket.getUserId(), myWebSocket.getUserBO());
        RedisNewUtil.expire(RedisKey.USER_ONLINE, "", RedisKey.USER_STATUS_EXPIRATION_TIME, TimeUnit.MINUTES);
    }

    public void removeCacheUser(MyWebSocket myWebSocket) {
        RedisNewUtil.mdel(RedisKey.USER_ONLINE, "", myWebSocket.getUserId());
    }

    public void saveUserStatus() {
        ConcurrentHashMap<Long, MyWebSocket> allOnline = GlobalWsMap.getAllOnline();
        Map<Object, Object> map = new HashMap<Object, Object>();
        Set<Map.Entry<Long, MyWebSocket>> entries = allOnline.entrySet();
        for (Map.Entry<Long, MyWebSocket> entry : entries) {
            map.put(entry.getKey().toString(), JSONObject.toJSONString(entry.getValue().getUserBO()));
        }
        RedisNewUtil.mput(RedisKey.USER_ONLINE, "", map, RedisKey.USER_STATUS_EXPIRATION_TIME, TimeUnit.MINUTES);
    }
}
