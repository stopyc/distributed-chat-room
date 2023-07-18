package org.example.pojo.bo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @program: monitor
 * @description: UserBO
 * @author: stop.yc
 * @create: 2023-04-06 16:29
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class UserBO implements Serializable {

    private static final long serialVersionUID = -40356785423868312L;

    private Long userId;

    private String username;

    private String password;

    private String gender;

    private String phone;

    private Integer age;

    private Integer userStatus;

    private Integer isDeleted;

    private String lastIp;
}
