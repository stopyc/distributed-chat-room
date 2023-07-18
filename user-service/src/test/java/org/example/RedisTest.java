package org.example;

import org.example.pojo.po.User;
import org.example.util.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

/**
 * @program: cloud
 * @description:
 * @author: stop.yc
 * @create: 2023-03-23 14:48
 **/
@SpringBootTest
public class RedisTest {


    @Autowired
    private RedisTemplate<Object, Object>  redisTemplate;

    @Autowired
    private RedisUtils redisUtils;
    @Test
    void test1() {
        redisTemplate.opsForValue()
                .set("user:1",new User());

        redisUtils.set("user:1", new User());

        User user = redisUtils.get("user:1", User.class);

        System.out.println("user = " + user);
    }

    @Test
    void test2() throws IOException {

    }
}
