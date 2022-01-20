package cn.edu.xmu.oomall.cart.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 购物车vo
 *
 * @author yang8miao
 * @date 2020/11/29 00:13
 * @version 1.0
 */
@Data
@ApiModel(description = "购物车接受对象")
public class CartVo {

    @NotNull(message = "商品skuId不能为空")
    @ApiModelProperty(value = "商品skuId")
    private Long goodsSkuId;

    @Min(value = 1, message = "数量不能小于0")
    @ApiModelProperty(value = "数量")
    private Integer quantity;

}
