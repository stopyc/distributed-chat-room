package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @program: cloud
 * @description:
 * @author: stop.yc
 * @create: 2023-03-20 21:27
 **/
@SpringBootTest
public class test {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    void test1() {
        String secret = passwordEncoder.encode("secret");
        System.out.println("secret = " + secret);

    }
}
