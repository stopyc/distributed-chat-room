package org.example.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.exception.BusinessException;
import org.omg.CORBA.SystemException;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

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
    public ResultDTO doSystemException(SystemException ex) {
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        ex.printStackTrace();
        log.error("异常: {}", ex.getMessage());
        return ResultDTO.fail(SERVER_INTERNAL_ERROR);
    }

    /**
     * 处理自定义异常BusinessException
     *
     */
    @ExceptionHandler(BusinessException.class)
    public ResultDTO doBusinessException(BusinessException ex) {
        ex.printStackTrace();
        log.error("异常: {}", ex.getMessage());
        return ResultDTO.fail(ex.getMessage());
    }

    /**
     * 处理数据参数异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    public ResultDTO doMethodArgumentNotValidException(Exception  e){

        log.error("异常:{}", e.getMessage());

        if (e instanceof MethodArgumentNotValidException ){

            BindingResult bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();

            if ( bindingResult.getFieldErrors().size() > 0) {

                FieldError error = bindingResult.getFieldErrors().get(0);
                String field = error.getField();
                Object value = error.getRejectedValue();
                String msg = error.getDefaultMessage();
                return ResultDTO.fail(msg);
            }

            //获取所有的错误集合,获取其提示信息,封装
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            return ResultDTO.fail(PARAMETER_NOT_VALID);
        }

        return ResultDTO.fail(e.getMessage());
    }

    /**
     * 处理异步线程抛出的异常
     * @param ex:异步线程封装的异常
     */
    @ExceptionHandler(CompletionException.class)
    public ResultDTO doCompletionException(CompletionException ex) {
        //异步线程只是多了一层封装,本质还是业务,系统和未知异常.

        ex.printStackTrace();
        log.error("发生了异常:", ex);

        ResultDTO resultVO = ResultDTO.fail(UNKNOWN_ERROR);

        if (ex.getCause() instanceof BusinessException) {
            resultVO = ResultDTO.fail(ex.getCause().getMessage());

        }
        else if (ex.getCause() instanceof SystemException) {
            resultVO = ResultDTO.fail(NETWORK_ERROR);

        }

        return resultVO;
    }


    /**
     * 处理认证异常
     */
    @ExceptionHandler(value = {AuthenticationException.class})
    public ResultDTO doAuthenticationException(AuthenticationException ex) {
        log.error("异常: {}", ex.getMessage());
        return ResultDTO.fail(ex.getMessage());
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    public ResultDTO doMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("异常: {}", ex.getMessage());
        return ResultDTO.fail("文件大小超出200MB");
    }


    @ExceptionHandler(value = {MultipartException.class})
    public ResultDTO doMultipartException(MultipartException ex) {
        log.error("异常: {}", ex.getMessage());
        return ResultDTO.fail("请上传文件喔");
    }

    /**
     * 除了自定义的异常处理器，保留对Exception类型的异常处理，用于处理非预期的异常
     **/
    @ExceptionHandler(Exception.class)
    public ResultDTO doOtherException(Exception ex) {
        log.error("异常:{}", ex.getMessage());


        //单独一个认证异常的处理
        if (ex instanceof AuthenticationException) {
            return ResultDTO.fail(AUTHENTICATION_FAILED);
        } else if (ex instanceof AccessDeniedException) {
            return ResultDTO.fail(FORBIDDEN);
        } else if (ex instanceof HttpMessageNotReadableException) {
            return ResultDTO.fail(JSON_FORMAT_ERROR);
        } else if (ex instanceof MissingServletRequestParameterException) {
            return ResultDTO.fail(PARAMETER_NOT_FOUND);
        } else if (ex instanceof RedisSystemException) {
            return ResultDTO.fail(NETWORK_ERROR);
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return ResultDTO.fail(REQUEST_METHOD_ERROR);
        }

        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        ex.printStackTrace();
        return ResultDTO.fail(NETWORK_ERROR);
    }
}
