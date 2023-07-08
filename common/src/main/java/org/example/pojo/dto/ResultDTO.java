package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.example.pojo.vo.StatusCode;

import java.io.Serializable;

import static org.example.constant.ResultEnum.BUSINESS_FAIL;
import static org.example.constant.ResultEnum.REQUEST_SUCCESS;


/**
 * @program: chat-room
 * @description:
 * @author: stop.yc
 * @create: 2022-11-08 21:07
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResultDTO implements Serializable {

    private static final long serialVersionUID = -3053272822257882225L;
    /**编号*/
    private Integer code;

    /**信息*/
    private String msg;

    /**数据*/
    private Object data;

    public static ResultDTO ok() {
        return new ResultDTO()
                .setCode(REQUEST_SUCCESS.getCode())
                .setMsg(REQUEST_SUCCESS.getMsg());
    }

    public static ResultDTO ok(Object data) {
        return new ResultDTO()
                .setCode(REQUEST_SUCCESS.getCode())
                .setMsg(REQUEST_SUCCESS.getMsg())
                .setData(data);
    }

    public static ResultDTO fail(StatusCode statusCode) {
        return new ResultDTO()
                .setCode(statusCode.getCode())
                .setMsg(statusCode.getMsg());
    }

    public static ResultDTO fail(StatusCode statusCode, Object data) {
        return new ResultDTO()
                .setCode(statusCode.getCode())
                .setMsg(statusCode.getMsg())
                .setData(data);
    }

    public static ResultDTO fail(String message) {
        return new ResultDTO()
                .setCode(BUSINESS_FAIL.getCode())
                .setMsg(message);
    }
}
