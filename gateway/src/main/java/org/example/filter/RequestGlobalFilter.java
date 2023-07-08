package org.example.filter;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.MonitorType;
import org.example.pojo.dto.ServiceLink;
import org.example.util.IpUtils;
import org.example.util.RedisUtils;
import org.example.util.TimeUtil;
import org.reactivestreams.Publisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.example.constant.GlobalConstants.*;

/**
 * 请求响应拦截,携带信息参数给下一个线程进行识别
 *
 * @author YC104
 */
@Slf4j
public class RequestGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisUtils redisUtils;

    @Value("${spring.application.name}")
    private String applicationName;


    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${project.name}")
    private String projectName ;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求
        ServerHttpRequest request = exchange.getRequest();

        //4. 获取线程id
        long threadId = Thread.currentThread().getId();

        //5. 添加线程id进请求头
        request = request.mutate().header(TRACE_ID, threadId + "").build();

        //6. 获取全局雪花id
        String requestId;

        requestId = UUID.randomUUID().toString();

        //7. 添加请求id进请求头
        request = request.mutate().header(REQUEST_ID, requestId + "").build();

        //8. 添加spanId进请求头 (因为是gateway放行,所以id为0)
        request = request.mutate().header(SPAN_ID, "0").build();

        //9.
        request = request.mutate().header(APPLICATION_NAME, applicationName).build();

        request = request.mutate().header(PROJECT_NAME, projectName).build();


        //9. 获取相关参数
        String path = request.getPath().value();
        String ip = IpUtils.extractIpStr(request);
        String method = request.getMethodValue();
        HttpHeaders header = request.getHeaders();
        String requestParams = String.valueOf(request.getQueryParams());
        AtomicReference<String> requestBody = new AtomicReference<>("");
        log.info("***********************************请求信息**********************************");
        log.info("当前的服务名称为 = {}", applicationName);
        log.info("PROJECT_NAME 为: {}", projectName);
        log.info("当前请求的ip地址为 = {}", ip);
        log.info("生成的唯一requestId = {}", requestId);
        log.info("当前threadId = {}", threadId);
        log.info("当前spanId = {}", "0");
        log.info("请求path = {}", path);
        log.info("请求header = {}", header);
        log.info("请求method = {}", method);
        log.info("请求requestParams = {}", requestParams);
        log.info("*********************************************************************");


        Map<String, String[]> paramsMap = null;
        //if ("GET".equals(method)) {
        //    MultiValueMap<String, String> queryParams = request.getQueryParams();
        //    paramsMap = new HashMap<>(queryParams.size());
        //
        //    for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
        //        String[] s = {entry.getValue().get(0)};
        //        paramsMap.put(entry.getKey(), s);
        //    }
        //} else {
        //    Flux<DataBuffer> body = request.getBody();
        //}


        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {

        }
        ServiceLink monitor = ServiceLink.builder()
                .monitorType(MonitorType.ServiceLink)
                .projectName(projectName)
                .currentApplicationName(applicationName)
                .time(TimeUtil.transfer(LocalDateTime.now(), String.class))
                .ip(ip)
                .frontApplicationName("nil")
                //.headers(header)
                .method(method)
                .requestId(requestId)
                .requestParams(paramsMap)
                .requestPath(path)
                .spanId("0")
                .traceId(threadId + "")
                .build();

        rabbitTemplate.convertAndSend("boot_topic_exchange", "boot.monitor", JSONObject.toJSONString(monitor));

        // . 放行指定路径,token太多,不进行展示
        if (!path.contains("/uaa/oauth/token")) {
            return chain.filter(exchange);
        }

        // 如果是文件,也不需要展示
        String contentType = header.getFirst("content-type");
        if (StringUtils.hasLength(contentType) && contentType.contains("multipart/form-data")) {
            return chain.filter(exchange);
        }

        //response返回内容
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                body = fluxBody.buffer().map(dataBuffers -> {
                    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                    DataBuffer join = dataBufferFactory.join(dataBuffers);
                    byte[] content = new byte[join.readableByteCount()];
                    join.read(content);
                    String responseData = new String(content, StandardCharsets.UTF_8);
                    log.info("***********************************响应信息**********************************");
                    log.info("响应内容:{}", responseData);
                    log.info("****************************************************************************\n");
                    DataBufferUtils.release(join);

                    //TODO 使用@Ansync异步方法日志入库
                    //修改返回内容，返回内容是JSON字符串，因此需要把JSON转成具体的对象再处理。
                    //R r = om.readValue(responseData, R.class);//R是统一泛型返回对象，这里因人而已，不具体介绍。
                    //String newContent = om.writeValueAsString(r);
                    //return bufferFactory.wrap(newContent.getBytes());
                    return bufferFactory.wrap(content);
                });
                return super.writeWith(body);
            }
        };
        log.info("****************************************************************************\n");

        //获取body，虽然该方法在后面，但是实际效果是在response前面
        if (header.getContentLength() > 0) {
            return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                String bodyString = new String(bytes, StandardCharsets.UTF_8);
                //设置requestBody到变量，让response获取
                requestBody.set(bodyString);
                log.info("requestBody = {}", bodyString);
                exchange.getAttributes().put("POST_BODY", bodyString);
                DataBufferUtils.release(dataBuffer);
                Flux<DataBuffer> cachedFlux = Flux.defer(() -> Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));

                ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedFlux;
                    }
                };
                return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build());
            });
        }

        //没有获取BODY，不用处理request
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }


    private String generateUUID(String ip) {


        System.out.println("ip = " + ip);
        long threadId = Thread.currentThread().getId();
        System.out.println("threadId = " + threadId);
        LocalDateTime now = LocalDateTime.now();
        long time = now.toEpochSecond(ZoneOffset.UTC);

        System.out.println("time = " + time);
        Long id = redisUtils.getId("trace:");
        System.out.println("id = " + id);

        return ip + time + id + threadId;
    }




    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
