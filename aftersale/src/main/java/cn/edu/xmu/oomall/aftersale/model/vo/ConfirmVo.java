package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/12/09 10:04
 */
@Data
@ApiModel(description = "售后审核对象")
public class ConfirmVo {
    @NotNull(message = "处理操作不能为空")
    @ApiModelProperty(value = "是否同意")
    private Boolean confirm;
    //@NotEmpty(message = "处理意见不能为空")
    @ApiModelProperty(value = "处理意见")
    private String conclusion;
    @NotNull(message = "处理金额不能为空")
    @ApiModelProperty(value = "金额")
    private Long price;
    @ApiModelProperty(value = "售后类型")
    private Integer type;
}
