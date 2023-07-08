package org.example.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;

import static org.example.constant.GlobalConstants.INTERNAL_CALL_REQUEST_HEADER_KEY;
import static org.example.constant.GlobalConstants.INTERNAL_CALL_REQUEST_HEADER_VALUE;
import static org.example.constant.ResultEnum.PROHIBIT_ACCESS;

/**
 * @program: monitor
 * @description: 内部接口认证
 * @author: stop.yc
 * @create: 2023-04-16 16:29
 **/
@Aspect
@Slf4j
@Component
@Order(101)
public class InnerControllerAspect {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Pointcut("@within(org.example.annotation.Inner) || @annotation(org.example.annotation.Inner)")
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object checkToken(ProceedingJoinPoint joinPoint) throws Throwable {
        //检查请求头是否有inner Header的标识,此处设置在feign进行远程调用的请求拦截器中,
        //对每个进行feign远程调用的都是添加上请求头,防止被外部接口访问
        if (request != null) {
            String header = request.getHeader(INTERNAL_CALL_REQUEST_HEADER_KEY);
            if (!StringUtils.hasText(header) || !INTERNAL_CALL_REQUEST_HEADER_VALUE.equals(header)) {
                throw new BusinessException(PROHIBIT_ACCESS);
            }
        }
        return joinPoint.proceed();
    }
}
