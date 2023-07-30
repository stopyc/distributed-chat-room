package org.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.ChatRoomMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.po.ChatRoom;
import org.example.pojo.po.User;
import org.example.service.IChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2023-07-30 12:14
 **/
@SpringBootTest
@Slf4j
public class ChatRoomTest extends ServiceImpl<ChatRoomMapper, ChatRoom> implements IChatRoomService {
    @Resource
    private UserMapper usermapper;

    @Resource
    private ChatRoomMapper chatRoomMapper;

    @Test
    void test1() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(User::getUserId);
        List<User> userList = usermapper.selectList(wrapper);
        log.info("userList 为: {}", userList);
        List<ChatRoom> chatRoomList = new ArrayList<>(userList.size());
        userList
                .forEach(user -> {
                    chatRoomList.add(new ChatRoom(1L, user.getUserId()));
                });
        boolean b = saveBatch(chatRoomList);

    }
}
