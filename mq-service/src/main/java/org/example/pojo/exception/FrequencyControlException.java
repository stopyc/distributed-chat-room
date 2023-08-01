package org.example.pojo.exception;


import org.example.pojo.vo.StatusCode;

/**
 * @program: Software-management-platform
 * @description: 自定义异常，用于封装异常信息，对异常进行分类
 * @author: stop.yc
 * @create: 2022-07-24 19:10
 **/
public class FrequencyControlException extends RuntimeException {
    private Integer code;

    public FrequencyControlException(StatusCode statusCode) {
        super(statusCode.getMsg());
        this.code = statusCode.getCode();
    }

    public FrequencyControlException(StatusCode statusCode, Throwable cause) {
        super(statusCode.getMsg(), cause);
        this.code = statusCode.getCode();
    }

    public FrequencyControlException(String msg) {
        super(msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
