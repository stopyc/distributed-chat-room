//package org.example.aop;
//
//import io.seata.core.context.RootContext;
//import io.seata.core.exception.TransactionException;
//import io.seata.tm.api.GlobalTransaction;
//import io.seata.tm.api.GlobalTransactionContext;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//
//@Aspect
//@Component
//@Slf4j
//public class TestAspect {
//
//    @Before("execution(* org.example.service.*.*(..))")
//    public void before(JoinPoint joinPoint) throws TransactionException {
//        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
//        Method method = signature.getMethod();
//        log.info("拦截到需要分布式事务的方法," + method.getName());
//        // 此处可用redis或者定时任务来获取一个key判断是否需要关闭分布式事务
//        // 模拟动态关闭分布式事务
//        if ((int)(Math.random() * 100) % 2 == 0) {
//            GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
//            tx.begin(300000, "test-client");
//        } else {
//            log.info("关闭分布式事务");
//        }
//    }
//
//    @AfterThrowing(throwing = "e", pointcut = "execution(* org.example.service.*.*(..))")
//    public void doRecoveryActions(Throwable e) throws TransactionException {
//        log.info("方法执行异常:{}", e.getMessage());
//        if (!StringUtils.isBlank(RootContext.getXID()))
//            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
//    }
//
//    @AfterReturning(value = "execution(* org.example.service.*.*(..))", returning = "result")
//    public void afterReturning(JoinPoint point, Object result) throws TransactionException {
//        log.info("方法执行结束:{}", result);
//        if ((Boolean)result) {
//            if (!StringUtils.isBlank(RootContext.getXID())) {
//                log.info("分布式事务Id:{}", RootContext.getXID());
//                GlobalTransactionContext.reload(RootContext.getXID()).commit();
//            }
//        }
//    }
//
//}