package cn.edu.xmu.oomall.address.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 地址Vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:03
 */
@Data
@ApiModel(description = "地址接受对象")
public class AddressVo {
    @NotNull(message = "地区码不能为空")
    @ApiModelProperty(value = "地区码")
    @Min(1)
    private Long regionId;
    @NotEmpty(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址")
    private String detail;
    @NotEmpty(message = "联系人不能为空")
    @ApiModelProperty(value = "联系人")
    private String consignee;
    @Pattern(regexp = "1\\d{10}", message = "电话不符合要求")
    @ApiModelProperty(value = "电话")
    private String mobile;
}
