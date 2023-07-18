package org.example.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: chat-room
 * @description: 用户类
 * @author: stop.yc
 * @create: 2023-01-09 19:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = -40356785423868312L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private String username;

    private String password;

    private String gender;

    private String phone;

    private Integer age;

    private Integer isDeleted;

    private String lastIp;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
