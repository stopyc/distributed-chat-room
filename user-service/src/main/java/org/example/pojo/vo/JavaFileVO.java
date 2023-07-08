package org.example.pojo.vo;

/**
 * @program: monitor
 * @description: 获取java源码信息的vo对象
 * @author: stop.yc
 * @create: 2023-04-17 11:17
 **/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class JavaFileVO {

    private String currentApplicationName;

    private String fileName;

    private String lineNumber;

    private String className;

    private String methodName;

}
