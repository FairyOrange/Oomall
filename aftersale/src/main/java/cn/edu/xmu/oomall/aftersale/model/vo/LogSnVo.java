package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 运单号Vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:03
 */
@Data
@ApiModel(description = "运单号接受对象")
public class LogSnVo {
    @NotNull(message = "运单号不能为空")
    @ApiModelProperty(value = "运单号")
    private String logSn;
}
