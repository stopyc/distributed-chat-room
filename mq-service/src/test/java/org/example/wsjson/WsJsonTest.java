package org.example.wsjson;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2023-08-09 16:04
 **/
@SpringBootTest
public class WsJsonTest {


    @Test
    public void test1() {
        String json = "{\n" +
                "  \"messageType\": 1,\n" +
                "  \"data\": {\n" +
                "    \"message\": 666,\n" +
                "    \"to\": 1341234\n" +
                "  }\n" +
                "}";

        JSONObject jsonObject = JSONObject.parseObject(json);
        Object messageType = jsonObject.get("messageType");
        System.out.println("messageType = " + messageType);
        Object o = jsonObject.get("data");
        System.out.println("o = " + o);
        DataVO dataVO = JSONObject.parseObject(o.toString(), DataVO.class);
        System.out.println("dataVO = " + dataVO);
    }
}
