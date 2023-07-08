package org.example.pojo.exception;


import org.example.pojo.vo.StatusCode;

/**
 * @program: aop_annotation
 * @description: 系统异常
 * @author: stop.yc
 * @create: 2022-08-09 22:14
 **/
public class SystemException extends RuntimeException{

    private StatusCode statusCode;

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setCode(StatusCode code) {
        this.statusCode = code;
    }

    public SystemException(StatusCode statusCode, Throwable cause) {
        super(statusCode.getMsg(), cause);
        this.statusCode = statusCode;
    }

    public SystemException(String message) {
        super(message);
    }
}
