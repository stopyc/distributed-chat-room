package com.alibaba.nacos.naming.utils.nacoshashring.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: chat-room
 * @description: 网络地址
 * @author: stop.yc
 * @create: 2023-07-18 11:03
 **/
@Data
@EqualsAndHashCode
public class Address implements Cloneable {

    /**
     * host
     */
    private String ip;

    /**
     * port
     */
    private Integer port;

    /**
     * 是否是虚拟结点
     */
    private Boolean virtualNode;

    /**
     * 包括虚拟结点在内的第几个结点
     */
    private Integer order;

    @Override
    public Address clone() throws CloneNotSupportedException {
        return (Address) super.clone();
    }
}