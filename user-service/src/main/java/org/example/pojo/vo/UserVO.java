package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static org.example.constant.RegexConstant.REGEX_PHONE_NUMBER;

/**
 * @program: security-demo
 * @description: 用户VO类
 * @author: stop.yc
 * @create: 2023-02-20 22:58
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class UserVO implements Serializable {

    private static final long serialVersionUID = -3462847124376859912L;

    @NotBlank(message = "请输入正确的用户名!")
    private String username;

    @NotBlank(message = "请输入正确的密码!")
    @Length(min = 6, max = 20 ,message = "密码长度不能小于6位或者大于20位!")
    private String password;

    @NotBlank(message = "请输入正确的性别!")
    private String gender;

    @Pattern(regexp = REGEX_PHONE_NUMBER, message = "请输入正确的手机号码!")
    private String phone;

    @Range(min = 0, max = 150)
    private Integer age;

    private String lastIp;
}
