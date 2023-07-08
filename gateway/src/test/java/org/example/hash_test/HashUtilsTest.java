package org.example.hash_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.util.GateWayHashUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @program: chat-room
 * @description: 哈希环测试类
 * @author: stop.yc
 * @create: 2023-07-04 15:25
 **/
@SpringBootTest
public class HashUtilsTest {

    @Resource
    private NewHashUtils newHashUtils;

    @Resource
    private GateWayHashUtils gateWayHashUtils;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    void test1() {
        newHashUtils.updateHashRingIsRegister("namespace",
                "ws-service",
                "127.0.0.1",
                8080,
                true,
                true);
    }

    @Test
    void test2() {
        gateWayHashUtils.getHashRingFromRedis();
        String server = GateWayHashUtils.getServer("1");
        System.out.println("server = " + server);
    }

    @Test
    void test3() {
        redisTemplate.opsForZSet().remove("hashring:",
                "127.0.0.1:8080&&VN2");
    }

    @Test
    void teset4() {
        redisTemplate.opsForZSet().add("hashring:", new Address("127.0.0.1", 8080), 1.0);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().rangeByScoreWithScores("hashring:", 0, 100);
        for (ZSetOperations.TypedTuple<Object> tuple : typedTuples) {
            Address value = (Address) tuple.getValue();
            System.out.println("value = " + value);
        }
    }
}
