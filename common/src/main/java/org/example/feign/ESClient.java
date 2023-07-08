package org.example.feign;

import org.example.pojo.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author YC104
 */
@FeignClient(value = "es-service")
public interface ESClient {

    @GetMapping("/es/public/list")
    String r2();
}
