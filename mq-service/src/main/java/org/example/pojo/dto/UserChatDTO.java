package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: chat-room
 * @description: 聊天室中用户DTO
 * @author: stop.yc
 * @create: 2023-04-19 21:02
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class UserChatDTO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 性别
     */
    private String gender;

    /**
     * 信用分
     */
    private Integer creditScore;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 剧本中的角色id
     */
    private Long roleId;

    /**
     * 剧本中的角色名称
     */
    private String roleName;

    /**
     * 用来统计参与度的,比如计算打字个数,语音中文字信息长短
     */
    private Long speech;

    /**
     * 是否在线,比如说,临时对出app,断开了ws,导致下线.0表示下线,1表示在线
     */
    private Integer online;
}
