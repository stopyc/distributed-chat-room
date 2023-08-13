package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: chat-room
 * @description: 艾特用户的dto
 * @author: stop.yc
 * @create: 2023-08-11 22:53
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class AtUserDTO {
    private Long userId;

    private String username;

    private String color;

    private String icon;
}
