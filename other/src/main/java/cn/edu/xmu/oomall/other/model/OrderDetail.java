package cn.edu.xmu.oomall.other.model;

import lombok.Data;

@Data
public class OrderDetail {
    private Long orderid;
    private String orderPrice;
    private String orderSku;
}
