package org.example.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: security-demo
 * @description: 权限类
 * @author: stop.yc
 * @create: 2023-01-09 20:36
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_menu")
@Builder

public class Menu implements Serializable {
    private static final long serialVersionUID = -54979041104113736L;
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    private String menuName;

    private String menuKey;

    private Long parentId;
}
