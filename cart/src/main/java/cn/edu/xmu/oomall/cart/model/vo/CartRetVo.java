package cn.edu.xmu.oomall.cart.model.vo;


import cn.edu.xmu.oomall.cart.model.bo.Cart;
import cn.edu.xmu.oomall.cart.model.bo.CouponInfo;
import cn.edu.xmu.oomall.model.VoObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车返回vo对象
 * @author yang8miao
 * @date Created in 2020/11/29 19:10
 */
@Data
@ApiModel(description = "购物车返回对象")
public class CartRetVo {

    @ApiModelProperty(value = "购物车id")
    private Long id;

    @ApiModelProperty(value = "商品skuId")
    private Long goodsSkuId;

    @ApiModelProperty(value = "商品sku名称")
    private String skuName;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "加入单价")
    private Long price;

    @ApiModelProperty(value = "优惠活动返回参数列表")
    private List<CouponInfo> couponActivity;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;


    /**
     * 用bo构造vo
     * @param cart
     */
    public CartRetVo(Cart cart){
        this.setId(cart.getId());
        this.setGoodsSkuId(cart.getGoodsSkuId());
        this.setSkuName(cart.getSkuName());
        this.setQuantity(cart.getQuantity());
        this.setPrice(cart.getPrice());
        this.setCouponActivity(cart.getCouponActivity());
        this.setGmtCreate(cart.getGmtCreate());
        this.setGmtModified(cart.getGmtModified());
    }
}
