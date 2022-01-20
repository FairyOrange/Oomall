package cn.edu.xmu.oomall.aftersale.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 售后修改vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/21 20:54
 */
@Data
@ApiModel(description = "售后修改对象")
public class AftersaleUpdateVo {
    @Min(value = 1, message = "数量不能小于0")
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    @ApiModelProperty(value = "原因")
    private String reason;
    @ApiModelProperty(value = "地区码")
    private Long regionId;
    @ApiModelProperty(value = "详细地址")
    private String detail;
    @ApiModelProperty(value = "联系人")
    private String consignee;
    @Pattern(regexp = "1\\d{10}", message = "电话不符合要求")
    @ApiModelProperty(value = "电话")
    private String mobile;
}
