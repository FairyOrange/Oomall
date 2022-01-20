package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 售后vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/21 20:54
 */
@Data
@ApiModel(description = "售后接受对象")
public class AftersaleVo {
    @ApiModelProperty(value = "服务类型")
    @NotNull(message = "服务类型不为空")
    private Byte type;
    @Min(value = 1, message = "数量不能小于1")
    @NotNull(message = "数量不能为空")
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    //@NotEmpty(message = "原因不能为空")
    @ApiModelProperty(value = "原因")
    private String reason;
    //@NotNull(message = "地区码不能为空")
    @ApiModelProperty(value = "地区码")
    private Long regionId;
    //@NotEmpty(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址")
    private String detail;
    @NotEmpty(message = "联系人不能为空")
    @ApiModelProperty(value = "联系人")
    private String consignee;
    @Pattern(regexp = "1\\d{10}", message = "电话不符合要求")
    @NotNull(message = "电话不能为空")
    @ApiModelProperty(value = "电话")
    private String mobile;
}
