package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @program: chat-room
 * @description: 自定义oauth2 token对象
 * @author: stop.yc
 * @create: 2023-07-31 15:19
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class TokenDTO {
    private String token;
    private Long userId;
    private String color;
}