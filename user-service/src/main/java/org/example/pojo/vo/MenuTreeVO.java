package org.example.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @program: security-demo
 * @description: 权限树状VO
 * @author: stop.yc
 * @create: 2023-02-21 20:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class MenuTreeVO implements Serializable {

    private static final long serialVersionUID = -2058171374154378580L;
    private Long menuId;

    private String menuName;

    private String menuKey;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MenuTreeVO> childList;
}
