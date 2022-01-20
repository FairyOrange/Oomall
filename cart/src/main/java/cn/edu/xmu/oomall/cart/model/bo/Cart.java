package cn.edu.xmu.oomall.cart.model.bo;


import cn.edu.xmu.oomall.cart.model.po.CartPo;
import cn.edu.xmu.oomall.cart.model.vo.CartRetVo;
import cn.edu.xmu.oomall.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车Bo
 *
 * @author yang8miao
 * @date 2020/11/29 11:47
 * @version 1.0
 */
@Data
public class Cart implements VoObject {


    private Long id;

    private Long customerId;

    private Long goodsSkuId;

    private Integer quantity;

    private Long price;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private String skuName;

    private String spuName;

    private List<CouponInfo> couponActivity;

    public Cart() {}

    public CartPo createPo(){
        CartPo po = new CartPo();
        po.setId(this.getId());
        po.setCustomerId(this.getCustomerId());
        po.setGoodsSkuId(this.getGoodsSkuId());
        po.setQuantity(this.getQuantity());
        po.setPrice(this.getPrice());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }

    /**
     * 通过po构造bo
     *
     * @param po po对象
     * @author yang8miao
     * @date 2020/11/29 11:51
     * @version 1.0
     */
    public Cart(CartPo po) {
        this.setId(po.getId());
        this.setCustomerId(po.getCustomerId());
        this.setGoodsSkuId(po.getGoodsSkuId());
        this.setQuantity(po.getQuantity());
        this.setPrice(po.getPrice());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }

    @Override
    public Object createVo() {
        return new CartRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
