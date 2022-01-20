package cn.edu.xmu.oomall.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户修改密码vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
@ApiModel(description = "用户修改密码对象")
public class ModifyPasswordVo {
    @NotBlank
    @ApiModelProperty(name = "验证码")
    private String captcha;
    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#@!~%^&*_$])[A-Za-z\\d#@!~%^&*_$]{6,6}", message = "密码不符合规范")
    @NotBlank
    @ApiModelProperty(name = "新密码")
    private String newPassword;
}
