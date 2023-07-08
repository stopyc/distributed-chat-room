package org.example.pojo.dto;

/**
 * @program: monitor
 * @description: 监控参数类
 * @author: stop.yc
 * @create: 2023-04-06 18:47
 **/

import lombok.*;
import lombok.experimental.Accessors;
import org.example.constant.MonitorType;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode()
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Monitor implements Serializable {

    private static final long serialVersionUID = 41521491724055808L;

    protected String projectName;

    protected String currentApplicationName;

    protected MonitorType monitorType;

    protected String time;

}
