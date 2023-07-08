package org.example.util.hashring_util.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: util
 * @description: 网络地址
 * @author: stop.yc
 * @create: 2023-07-06 15:44
 **/
@Data
@EqualsAndHashCode
public class Address implements Cloneable{

    private String ip;

    private Integer port;

    private Boolean virtualNode;

    private Integer order;

    @Override
    public Address clone() throws CloneNotSupportedException {
        return (Address)super.clone();
    }
}
