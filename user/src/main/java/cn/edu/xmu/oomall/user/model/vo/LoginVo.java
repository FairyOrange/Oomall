package cn.edu.xmu.oomall.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
@ApiModel(description = "用户登录对象")
public class LoginVo {
    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(name = "用户名")
    private String userName;
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(name = "密码")
    private String password;
}
