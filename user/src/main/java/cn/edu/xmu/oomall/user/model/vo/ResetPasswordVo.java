package cn.edu.xmu.oomall.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户重置密码vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
@ApiModel(description = "用户重置密码对象")
public class ResetPasswordVo {
    @NotBlank
    @ApiModelProperty(name = "用户名")
    private String userName;
    @NotBlank
    @ApiModelProperty(name = "邮箱")
    private String email;
//    @NotBlank
//    @ApiModelProperty(name = "手机号")
//    private String mobile;
}
