package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @program: security-demo
 * @description: 权限VO
 * @author: stop.yc
 * @create: 2023-02-21 20:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class MenuVO implements Serializable {

    private static final long serialVersionUID = -945611322427353747L;
    @NotBlank(message = "权限名称不能为空")
    private String  menuName;

    @NotBlank(message = "权限关键字不能为空")
    private String menuKey;

    private Long parentId;

}
