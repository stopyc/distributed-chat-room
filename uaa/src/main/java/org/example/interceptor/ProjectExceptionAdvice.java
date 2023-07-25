package org.example.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.vo.ResultVO;
import org.omg.CORBA.SystemException;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.CompletionException;

import static org.example.constant.ResultEnum.*;


/**
 * @program: Software-management-platform
 * @description: 全局异常处理器
 * @author: stop.yc
 * @create: 2022-07-24 19:10
 **/

@RestControllerAdvice
@Slf4j
@Order(1)
public class ProjectExceptionAdvice {

/**
      日志打印
**/


    /**
     * 处理自定义异常SystemException
     **/
    @ExceptionHandler(SystemException.class)
    public ResultVO doSystemException(SystemException ex) {
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        ex.printStackTrace();
        log.error("异常: {}", ex.getMessage());
        return ResultVO.fail(SERVER_INTERNAL_ERROR, ex.getMessage());
    }

    /**
     * 处理自定义异常BusinessException
     *
     */
    @ExceptionHandler(BusinessException.class)
    public ResultVO doBusinessException(BusinessException ex) {
        ex.printStackTrace();
        log.error("异常: {}", ex.getMessage());
        return ResultVO.fail(BUSINESS_FAIL, ex.getMessage());
    }

    /**
     * 处理数据参数异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResultVO doMethodArgumentNotValidException(MethodArgumentNotValidException  e){

        e.printStackTrace();
        log.error("异常:{}", e.getMessage());

        BindingResult bindingResult = e.getBindingResult();
        if ( bindingResult.getFieldErrors().size() > 0){

            FieldError error = bindingResult.getFieldErrors().get(0);
            String field = error.getField();
            Object value = error.getRejectedValue();
            String msg = error.getDefaultMessage();
            //获取所有的错误集合,获取其提示信息,封装
            return ResultVO.fail(PARAMETER_NOT_VALID,msg);
        }
        return ResultVO.fail(PARAMETER_NOT_VALID,e.getMessage());
    }

    /**
     * 处理异步线程抛出的异常
     * @param ex:异步线程封装的异常
     */
    @ExceptionHandler(CompletionException.class)
    public ResultVO doCompletionException(CompletionException ex) {
        //异步线程只是多了一层封装,本质还是业务,系统和未知异常.

        ex.printStackTrace();
        log.error("发生了异常:", ex);

        ResultVO resultVO = ResultVO.fail(UNKNOWN_ERROR, ex.getCause().getMessage());

        if (ex.getCause() instanceof BusinessException) {
            resultVO = ResultVO.fail(BUSINESS_FAIL, ex.getMessage());

        }
        else if (ex.getCause() instanceof SystemException) {
            resultVO = ResultVO.fail(SERVER_INTERNAL_ERROR, ex.getMessage());

        }

        return resultVO;
    }



    /**
     * 处理认证异常
     */
    @ExceptionHandler(value = {AuthenticationException.class})
    public ResultVO doAuthenticationException(AuthenticationException  ex){
        ex.printStackTrace();
        log.error("异常: {}", ex.getMessage());
        return ResultVO.fail(AUTHENTICATION_FAILED,ex.getMessage());
    }

    /** 除了自定义的异常处理器，保留对Exception类型的异常处理，用于处理非预期的异常 **/
    @ExceptionHandler(Exception.class)
    public ResultVO doOtherException(Exception ex){
        ex.printStackTrace();
        log.error("异常:{}", ex.getMessage());


        //单独一个认证异常的处理
        if (ex instanceof AuthenticationException) {
            return ResultVO.fail(AUTHENTICATION_FAILED,ex.getMessage());
        } else if (ex instanceof AccessDeniedException) {
            return ResultVO.fail(FORBIDDEN,ex.getMessage());
        } else if (ex instanceof HttpMessageNotReadableException) {
            return ResultVO.fail(JSON_FORMAT_ERROR,ex.getMessage());
        } else if (ex instanceof MissingServletRequestParameterException) {
            return ResultVO.fail(PARAMETER_NOT_FOUND, ex.getMessage());
        }


        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        ex.printStackTrace();
        return new ResultVO(SERVER_INTERNAL_ERROR.getCode(), SERVER_INTERNAL_ERROR.getMsg(),null);
    }
}
