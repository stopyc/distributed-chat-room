package org.example.pojo.exception;


import org.example.pojo.vo.StatusCode;

/**
 * @program: aop_annotation
 * @description: 远程调用异常
 * @author: stop.yc
 * @create: 2022-08-09 22:14
 **/
public class FeignException extends RuntimeException{

    private StatusCode statusCode;

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setCode(StatusCode code) {
        this.statusCode = code;
    }

    public FeignException(StatusCode statusCode, Throwable cause) {
        super(statusCode.getMsg(), cause);
        this.statusCode = statusCode;
    }

    public FeignException(String message) {
        super(message);
    }
}
