package cn.edu.xmu.oomall.favorite.model.bo;


import cn.edu.xmu.oomall.favorite.model.po.FavoritePo;
import cn.edu.xmu.oomall.favorite.model.vo.FavoriteRetVo;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏Bo
 *
 * @author yang8miao
 * @date 2020/11/28 20:10
 * @version 1.0
 */
@Data
public class Favorite implements VoObject {

    private Long id;

    private Long customerId;

    private Long goodsSkuId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private SkuInfo goodsSku;


    public Favorite() {}

    public FavoritePo createPo(){
        FavoritePo po = new FavoritePo();
        po.setId(this.getId());
        po.setCustomerId(this.getCustomerId());
        po.setGoodsSkuId(this.getGoodsSkuId());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }

    /**
     * 通过po构造bo
     *
     * @param po po对象
     * @author yang8miao
     * @date 2020/11/28 20:43
     * @version 1.0
     */
    public Favorite(FavoritePo po) {
        this.setId(po.getId());
        this.setCustomerId(po.getCustomerId());
        this.setGoodsSkuId(po.getGoodsSkuId());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }


    /**
     * 用bo对象创造vo对象
     * @return FavoriteRetVo
     * @author yang8miao
     * @date Created in 2020/11/28 20:43
     */
    @Override
    public Object createVo() {
        return new FavoriteRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
