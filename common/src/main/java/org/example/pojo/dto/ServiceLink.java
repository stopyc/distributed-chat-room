package org.example.pojo.dto;

import lombok.*;
import lombok.experimental.Accessors;
import org.example.constant.MonitorType;

import java.util.Map;

/**
 * @program: monitor
 * @description: 服务链路监控
 * @author: stop.yc
 * @create: 2023-04-06 18:58
 **/
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class ServiceLink extends Monitor{

    private static final long serialVersionUID = -1613699932650947388L;

    protected String ip;

    protected String requestId;

    protected String traceId;

    protected String spanId;

    protected String requestPath;

    protected Map<String, String> headers;

    protected String method;

    protected Map<String, String[]> requestParams;

    protected String frontApplicationName;

    @Builder
    public ServiceLink(String projectName, String currentApplicationName, MonitorType monitorType, String time, String ip, String requestId, String traceId, String spanId, String requestPath, Map<String, String> headers, String method, Map<String, String[]> requestParams, String frontApplicationName) {
        super(projectName,currentApplicationName, monitorType, time);
        this.ip = ip;
        this.requestId = requestId;
        this.traceId = traceId;
        this.spanId = spanId;
        this.requestPath = requestPath;
        this.headers = headers;
        this.method = method;
        this.requestParams = requestParams;
        this.frontApplicationName = frontApplicationName;
    }
}
