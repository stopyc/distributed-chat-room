package org.example.aop;//package org.example.aop;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.weaver.ast.Var;
import org.example.constant.MonitorType;
import org.example.pojo.dto.MyException;
import org.example.pojo.dto.ServiceLink;
import org.example.util.IpUtils;
import org.example.util.TimeUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.constant.GlobalConstants.*;

/**
 * @description: 监控接口异常的一个aop切面
 * @author YC104
 */
@Aspect
@Component
@Slf4j
@Order(100)
public class MonitorAspect {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Value("${spring.application.name}")
    private String currentApplicationName;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @AfterThrowing(throwing = "e", pointcut = "execution(* org.example.controller.*.*(..))")
    public void doRecoveryActions(Throwable e) throws TransactionException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString(); // 异常堆栈信息转换成字符串


        // 定义正则表达式，匹配以左括号开头、以右括号结尾的文本
        Pattern pattern = Pattern.compile("\\((.*?)\\)");

        Matcher matcher = pattern.matcher(stackTrace);

        StringBuffer sb = new StringBuffer();

        // 循环处理所有匹配项
        while (matcher.find()) {
            // 获取括号中的文本
            String text = matcher.group(1);

            // 在括号前添加 <a> 标签，将括号中的内容高亮显示
            matcher.appendReplacement(sb, "(<span style='color:red'>" + text + "</span>)");
        }

        // 将剩余文本添加到结果字符串中
        matcher.appendTail(sb);

        stackTrace = sb.toString();

        MyException exception = null;
        if (request != null) {
            String requestId = request.getHeader(REQUEST_ID);
            String traceId = request.getHeader(TRACE_ID);
            String spanId = request.getHeader(SPAN_ID);
            String frontApplicationName = request.getHeader(APPLICATION_NAME);
            String projectName = request.getHeader(PROJECT_NAME);

            String path = null;
            path = request.getRequestURI();
            try {

                path = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException ignored) {

            }
            exception = MyException.builder()
                    .projectName(projectName)
                    .currentApplicationName(currentApplicationName)
                    .monitorType(MonitorType.Exception)
                    .time(TimeUtil.transfer(LocalDateTime.now(), String.class))
                    .ip(IpUtils.extractIpStr(request))
                    .frontApplicationName(frontApplicationName)
                    .method(request.getMethod())
                    .requestId(requestId)
                    .requestParams(request.getParameterMap())
                    .requestPath(path)
                    .spanId(spanId)
                    .traceId(traceId)
                    .exceptionMessage(stackTrace)
                    .stackTraceElement(e.getStackTrace())
                    .build();
        }else {
            exception = MyException.builder()
                    .projectName(DEFAULT_PROJECT_NAME)
                    .currentApplicationName(currentApplicationName)
                    .monitorType(MonitorType.Exception)
                    .time(TimeUtil.transfer(LocalDateTime.now(), String.class))
                    .exceptionMessage(stackTrace)
                    .stackTraceElement(e.getStackTrace())
                    .build();
        }


        StackTraceElement[] stackTrace2 = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace2) {
            //System.out.println(stackTraceElement.toString());
        }
        rabbitTemplate.convertAndSend("boot_topic_exchange", "boot.monitor", JSONObject.toJSONString(exception));
    }

}