package cn.edu.xmu.oomall.share.model.bo;

import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fiber W.
 * created at 12/19/20 11:11 AM
 * @detail cn.edu.xmu.oomall.share.model.bo
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

    public SkuInfo(SkuInfoDTO skuInfoDTO) {
        this.id = skuInfoDTO.getId();
        this.name = skuInfoDTO.getName();
        this.skuSn = skuInfoDTO.getSkuSn();
        if (skuInfoDTO.getImageUrl() == null) {
            this.imageUrl = "";
        } else {
            this.imageUrl = skuInfoDTO.getImageUrl();
        }

        this.inventory = skuInfoDTO.getInventory();
        this.originalPrice = skuInfoDTO.getOriginalPrice();
        this.price = skuInfoDTO.getPrice();
        this.disable = skuInfoDTO.getDisable().equals((byte)1);
    }
}
