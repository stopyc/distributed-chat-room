package org.example.feign;

import org.example.config.FeignConfiguration;
import org.example.pojo.dto.ResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author YC104
 */
@FeignClient(configuration = FeignConfiguration.class, url = "http://47.113.186.65/", value = "id-service")
public interface IdClient {
    @GetMapping("/id/nextId/{strategyType}")
    ResultDTO nextId(@PathVariable("strategyType") String strategyType);
}
