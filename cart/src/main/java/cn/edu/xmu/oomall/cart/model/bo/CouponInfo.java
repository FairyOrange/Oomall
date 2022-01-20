package cn.edu.xmu.oomall.cart.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponInfo {

    /**
     * 优惠活动id
     */
    private Long id;
    private String name;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
