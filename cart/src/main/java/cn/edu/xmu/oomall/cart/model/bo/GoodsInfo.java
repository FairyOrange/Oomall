package cn.edu.xmu.oomall.cart.model.bo;

import cn.edu.xmu.oomall.goods.model.CouponInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfo {

    private String skuName;

    private Long spuId;

    /**
     * 该sku的所有优惠活动
     */
    private List<CouponInfo> couponActivity;
}
