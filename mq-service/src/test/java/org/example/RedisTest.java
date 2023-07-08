package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2023-05-05 09:49
 **/
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate<Object, Object>  redisTemplate;
    @Test
    void test1() {

        System.out.println("redisTemplate.opsForHash().delete(\"hashring:\", 1146782011) = " + redisTemplate.opsForHash().delete("hashring:", 1146782011));
        System.out.println("redisTemplate.opsForHash().delete(\"hashring:\", \"1476444295\") = " + redisTemplate.opsForHash().delete("hashring:", "1476444295"));

    }
}
