package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 审核Vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:03
 */
@Data
@ApiModel(description = "确认接受对象")
public class ReviewVo {
    @NotNull(message = "处理操作不能为空")
    @ApiModelProperty(value = "是否同意")
    private Boolean confirm;
    //@NotEmpty(message = "处理意见不能为空")
    @ApiModelProperty(value = "处理意见")
    private String conclusion;
}
