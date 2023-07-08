package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @program: security-demo
 * @description: 用户类
 * @author: stop.yc
 * @create: 2023-01-09 19:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -40356785423868312L;

    private String username;

    private String password;

}
