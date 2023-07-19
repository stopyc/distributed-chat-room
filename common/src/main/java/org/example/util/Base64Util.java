package org.example.util;

import java.util.Base64;

/**
 * @program: chat-room
 * @description: Base64编解码工具类
 * @author: stop.yc
 * @create: 2023-07-19 09:34
 **/
public class Base64Util {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private Base64Util() {

    }

    public static String encode(byte[] byteArray) {
        return ENCODER.encodeToString(byteArray);
    }

    public static byte[] decode(String base64String) {
        return DECODER.decode(base64String);
    }
}
