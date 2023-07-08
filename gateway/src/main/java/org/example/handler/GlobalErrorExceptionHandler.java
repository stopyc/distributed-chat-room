package org.example.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.example.pojo.vo.ResultVO;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.example.constant.ResultEnum.TOKEN_FAILURE;
import static org.example.constant.ResultEnum.UNAUTHORIZED;

/**
 * @program: cloud
 * @description: 网关全局异常拦截(不用动)
 * @author: stop.yc
 * @create: 2023-03-21 23:06
 **/
@Component
@Slf4j
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        ex.printStackTrace();


        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        ResultVO fail = ResultVO.fail(UNAUTHORIZED, ex.getMessage());

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof  ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }

        if (ex instanceof InvalidTokenException) {
            fail = ResultVO.fail(TOKEN_FAILURE);
        }

        ResultVO finalResult = fail;

        return response.writeWith(Mono.fromSupplier(()->{
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                return bufferFactory.wrap(new ObjectMapper().writeValueAsBytes(finalResult));
            } catch (Exception e) {
                log.error("响应失败 ",e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
