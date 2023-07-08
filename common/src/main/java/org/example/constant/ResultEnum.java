package org.example.constant;

import lombok.Getter;
import org.example.pojo.vo.StatusCode;

/**
 * 状态枚举
 * @author YC104
 */
@Getter
public enum ResultEnum implements StatusCode {
    //自定义
    //通用
    UNKNOWN_ERROR(-1,"服务器正在忙碌中,请稍后试试吧"),
    SUCCESS(200,"成功"),
    UNAUTHORIZED(401,"认证失败"),
    FORBIDDEN(403,"权限不足"),
    RESOURCE_NOT_FOUND(404,"资源未找到"),
    PARAMETER_NOT_VALID(400,"参数不合法"),
    SERVER_INTERNAL_ERROR(500,"您的网络异常,请稍后刷新页面重试~"),
    REQUEST_SUCCESS(200,"请求成功"),
    BUSINESS_FAIL(400,"业务失败"),


    PROHIBIT_ACCESS(402,"禁止访问"),
    TOKEN_FAILURE(40001,"token校验失败"),
    AUTHENTICATION_FAILED(401,"认证失败"),
    JSON_FORMAT_ERROR(40002,"json格式错误"),
    PARAMETER_NOT_FOUND(40003,"参数缺失"),
    USERNAME_OR_PASSWORD_ERROR(40004,"用户名或密码错误"),
    AUTHENTICATION_METHOD_ERROR(40005,"认证方式错误"),
    CLIENT_AUTHENTICATION_ERROR(40006,"客户端认证错误"),
    ALREADY_LOGGED(40007,"您已经登录了"),


    LOGIN_FAIL(40001,"登录失败"),
    PASSWORD_ERROR(40002,"密码错误"),
    TOKEN_ERROR(40004,"token无效"),
    REFRESH_TOKEN(40005,"token过期,请刷新令牌"),
    PIC_FORMAT_ERORR(40006,"图片格式只能是png、jpg、jpeg之一喔"),
    REQUEST_METHOD_ERROR(40007,"请求方式错误"),

    NETWORK_ERROR(50001,"您的网络状况不佳,请再试试喔~"),


    ;
    /**
     * 编号
     */
    private int code;
    /**
     * 信息
     */
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
