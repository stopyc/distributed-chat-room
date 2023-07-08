package org.example.feign;

import feign.Headers;
import org.example.annotation.Inner;
import org.example.config.FeignConfiguration;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author YC104
 */
@FeignClient(value = "user-service",configuration = FeignConfiguration.class)
public interface UserClient {
    /**
     * 内部开放接口,通过用户名获取用户对象
     * @param username: 用户名
     * @return :用户对象
     */
    @GetMapping("/user/inner/getUserByUsername/{username}")
    ResultVO getByUsername(@PathVariable("username") String username);

    /**
     * 内部开放接口,通过用户id获取用户对象
     * @param userId: 用户id
     * @return :用户对象
     */
    @GetMapping("/user/inner/getById")
    ResultDTO getById(@RequestParam("userId") Long userId) ;
}