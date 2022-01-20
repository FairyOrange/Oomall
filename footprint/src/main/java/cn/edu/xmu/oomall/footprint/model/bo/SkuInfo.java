package cn.edu.xmu.oomall.footprint.model.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SkuInfo 用来方便扩充与修改SkuInfoDTO
 *
 * @author yang8miao
 * @date 2020/12/12 00:02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuInfo {

    private Long id;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Integer inventory;
    private Long originalPrice;
    private Long price;
    private Boolean disable;


}
