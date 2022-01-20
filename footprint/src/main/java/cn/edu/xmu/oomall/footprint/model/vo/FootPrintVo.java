package cn.edu.xmu.oomall.footprint.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 足迹vo对象
 * @author yang8miao
 * @date Created in 2020/11/28 17:31
 */
@Data
@ApiModel(description = "足迹对象")
public class FootPrintVo {

    @NotNull(message = "商品skuId不能为空")
    @ApiModelProperty(value = "商品skuId")
    private Long goodsSkuId;

}
