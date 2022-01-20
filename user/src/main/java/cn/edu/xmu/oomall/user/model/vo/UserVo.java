package cn.edu.xmu.oomall.user.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * 用户vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
@ApiModel(description = "用户接受对象")
public class UserVo {
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "1\\d{10}", message = "电话不符合要求")
    @ApiModelProperty(value = "电话")
    private String mobile;
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱不符合要求")
    @ApiModelProperty(value = "邮箱")
    private String email;
    @NotBlank(message = "昵称不能为空")
    @ApiModelProperty(value = "昵称")
    private String userName;
    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#@!~%^&*_$])[A-Za-z\\d#@!~%^&*_$]{6,6}", message = "密码不符合规范")
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码")
    private String password;
    @NotBlank(message = "真实姓名不能为空")
    @ApiModelProperty(value = "真实姓名")
    private String realName;
    @NotNull
    @Range(min = 0, max = 1, message = "性别不符合要求")
    @ApiModelProperty(value = "性别")
    private Byte gender;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生日")
    private LocalDate birthday;
}