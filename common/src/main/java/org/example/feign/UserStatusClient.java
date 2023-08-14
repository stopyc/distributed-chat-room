package org.example.feign;

import org.example.config.FeignConfiguration;
import org.example.pojo.dto.ResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author YC104
 */
@FeignClient(value = "ws-service", configuration = FeignConfiguration.class)
public interface UserStatusClient {
    @GetMapping("/status/getChatRoomUserStatus/{chatRoomId}")
    ResultDTO getChatRoomUserStatus(@PathVariable("chatRoomId") Long chatRoomId);
}
