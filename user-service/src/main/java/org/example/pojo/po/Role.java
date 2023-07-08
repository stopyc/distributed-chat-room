package org.example.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: security-demo
 * @description: 角色类
 * @author: stop.yc
 * @create: 2023-01-09 20:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_role")
public class Role implements Serializable {
    private static final long serialVersionUID = 6912583589794162764L;

    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    private String roleName;

    private String roleKey;
}
