package org.example.utils;

import org.example.feign.IdClient;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.exception.SystemException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @program: chat-room
 * @description: 获取全局唯一id
 * @author: stop.yc
 * @create: 2023-07-19 10:56
 **/
@Component
public class IdUtil {

    @Resource
    private IdClient idClient;

    public long nextId() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future = this.nextIdInternal();
        CompletableFuture.allOf(future);
        return future.get();
    }


    @Async
    protected CompletableFuture<Long> nextIdInternal() {
        ResultDTO snowflake = idClient.nextId("snowflake");
        if (snowflake.getCode() != 200) {
            snowflake = idClient.nextId("snowflake");
            if (snowflake.getCode() != 200) {
                throw new SystemException("获取全局唯一id失败");
            }
        }
        return CompletableFuture.completedFuture((Long) snowflake.getData());
    }
}
