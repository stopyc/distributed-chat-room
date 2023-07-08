package org.example.util.hashring_util;


import org.example.util.hashring_util.entity.Address;
import org.example.util.hashring_util.support.HashRingRedis;

/**
 * @program: util
 * @description:
 * @author: stop.yc
 * @create: 2023-07-06 16:19
 **/
public class Test {
    public static void main(String[] args) {
        HashRing hashRing = HashRingRedis.newInstance(new WebsocketServer());
        System.out.println("hashRing.getServerName() = " + hashRing.getServerName());
        Address address = hashRing.getAddress("1");
        System.out.println("address = " + address);
    }
}
