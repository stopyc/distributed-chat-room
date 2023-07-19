package org.example.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import java.io.IOException;

/**
 * @author YC104
 */
@Slf4j
@Component
public class MonitorFilter implements Filter {


    @Value("${spring.application.name}")
    private String applicationName;

    @Resource
    private RabbitTemplate  rabbitTemplate;



    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        //TODO:服务链路监控,有空再搞
        //HttpServletRequest req = (HttpServletRequest) request;
        //
        ////2. 获取该次请求的id(由gateway或者上级服务携带)
        //String requestId = req.getHeader(REQUEST_ID);
        //
        ////3. 获取上级服务的线程id
        //String traceId = req.getHeader(TRACE_ID);
        //
        ////4. 获取上级服务的spanId
        //String spanId = req.getHeader(SPAN_ID);
        //
        //String projectName = req.getHeader(PROJECT_NAME);
        //
        ////5. 设置该服务线程id
        //String thisTraceId = Thread.currentThread().getId() + "";
        //
        ////6. 设置该服务spanId
        //String thisSpanId = spanId + ".0";
        //
        //String frontApplicationName = req.getHeader(APPLICATION_NAME);
        //
        //log.info("********************************微服务链路追踪**********************************");
        //
        //log.info("上级服务的名称为 {}",frontApplicationName);
        //
        //log.info("本级服务的名称为 {}",applicationName);
        //
        //log.info("上级服务传递的请求id为:{}, 线程id为:{}, spanId为:{}", requestId, traceId, spanId);
        //
        //log.info("该服务传递的线程id为:{}, spanId为:{}", thisTraceId, thisSpanId);
        //
        //String path = null;
        //path = req.getRequestURI();
        //try {
        //
        //    path = URLDecoder.decode(path, "UTF-8");
        //} catch (UnsupportedEncodingException ignored) {
        //
        //}
        //ServiceLink serviceLink = ServiceLink.builder()
        //        .projectName(projectName)
        //        .currentApplicationName(applicationName)
        //        .monitorType(MonitorType.ServiceLink)
        //        .time(TimeUtil.transfer(LocalDateTime.now(),String.class))
        //        .ip(IpUtils.extractIpStr(req))
        //        .frontApplicationName(frontApplicationName)
        //        .method(req.getMethod())
        //        .requestId(requestId)
        //        .requestParams(req.getParameterMap())
        //        .requestPath(path)
        //        .spanId(thisSpanId)
        //        .traceId(thisTraceId)
        //        .build();
        //
        //rabbitTemplate.convertAndSend("boot_topic_exchange", "boot.monitor", JSONObject.toJSONString(serviceLink));
        //
        //ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);
        //
        //chain.doFilter(request, responseWrapper);
        //
        //String responseBody = responseWrapper.getBody();
        //
        //log.info("该服务响应结果为:{}", responseBody);
        //
        //
        //// 将响应体回写入原本的 HttpServletResponse 中
        //ServletOutputStream outputStream = response.getOutputStream();
        //outputStream.write(responseBody.getBytes());
        //outputStream.flush();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}