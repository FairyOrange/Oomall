package cn.edu.xmu.oomall.favorite.model.vo;


import cn.edu.xmu.oomall.favorite.model.bo.Favorite;
import cn.edu.xmu.oomall.favorite.model.bo.SkuInfo;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏返回vo对象
 * @author yang8miao
 * @date Created in 2020/11/28 20:44
 */
@Data
@ApiModel(description = "收藏返回对象")
public class FavoriteRetVo {

    @ApiModelProperty(value = "收藏id")
    private Long id;

    @ApiModelProperty(value = "商品sku")
    private SkuInfo goodsSku;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    /**
     * 用bo构造vo
     * @param favorite
     */
    public FavoriteRetVo(Favorite favorite){
        this.setId(favorite.getId());
        this.setGoodsSku(favorite.getGoodsSku());
        this.setGmtCreate(favorite.getGmtCreate());
    }
}
