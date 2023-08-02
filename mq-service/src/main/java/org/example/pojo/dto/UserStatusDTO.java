package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: chat-room
 * @description: 用户状态DTO
 * @author: stop.yc
 * @create: 2023-08-02 12:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class UserStatusDTO {

    private Long userId;

    private String username;

    private String gender;

    private String color;

    private Boolean online;
}
