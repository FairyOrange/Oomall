package cn.edu.xmu.oomall.address.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 地区Vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:03
 */
@Data
@ApiModel(description = "地区接受对象")
public class RegionVo {
    @NotEmpty(message = "地区名称不能为空")
    @ApiModelProperty(value = "地区名称")
    private String name;
    @NotNull(message = "邮政编码不能为空")
    @ApiModelProperty(value = "邮政编码")
    private Long postalCode;
}
