package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static org.example.constant.ResultEnum.REQUEST_SUCCESS;


/**
 * @program: security-demo
 * @description:
 * @author: stop.yc
 * @create: 2022-11-08 21:07
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResultVO {

    /**编号*/
    private Integer code;

    /**信息*/
    private String msg;

    /**数据*/
    private Object data;

    public static ResultVO ok() {
        return new ResultVO()
                .setCode(REQUEST_SUCCESS.getCode())
                .setMsg(REQUEST_SUCCESS.getMsg());
    }

    public static ResultVO ok(Object data) {
        return new ResultVO()
                .setCode(REQUEST_SUCCESS.getCode())
                .setMsg(REQUEST_SUCCESS.getMsg())
                .setData(data);
    }

    public static ResultVO fail(StatusCode statusCode) {
        return new ResultVO()
                .setCode(statusCode.getCode())
                .setMsg(statusCode.getMsg());
    }

    public static ResultVO fail(StatusCode statusCode, Object data) {
        return new ResultVO()
                .setCode(statusCode.getCode())
                .setMsg(statusCode.getMsg())
                .setData(data);
    }
}
