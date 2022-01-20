package cn.edu.xmu.oomall.share.model.bo;

import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.model.po.SharePo;
import cn.edu.xmu.oomall.share.model.vo.ShareRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Fiber W.
 * created at 11/23/20 10:10 AM
 * @detail cn.edu.xmu.oomall.share.model.bo
 */
@Data
public class Share implements VoObject {
    private Long id;
    private Long sharerId;
    private Long goodsSkuId;
    private Integer quantity;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long shareActivityId;
    private SkuInfo sku;

    /**
     * 创建po
     * @return cn.edu.xmu.oomall.share.model.po.OtherSharePo
     * @author Fiber W.
     * created at 11/23/20 11:11 AM
     */
    public SharePo createPo() {
        SharePo sharePo = new SharePo();
        sharePo.setId(this.id);
        sharePo.setGoodsSkuId(this.goodsSkuId);
        sharePo.setQuantity(this.quantity);
        sharePo.setSharerId(this.sharerId);
        sharePo.setGmtCreate(this.gmtCreate);
        sharePo.setGmtModified(this.gmtModified);
        sharePo.setShareActivityId(this.shareActivityId);

        return sharePo;
    }

    public Share() {}

    /**
     * 构造函数
     * @param po OtherSharePo
     * @author Fiber W.
     * created at 11/23/20 11:12 AM
     */
    public Share(SharePo po) {
        this.goodsSkuId = po.getGoodsSkuId();
        this.id = po.getId();
        this.quantity = po.getQuantity();
        this.sharerId = po.getSharerId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.shareActivityId = po.getShareActivityId();
    }

    /**
     * 用bo对象创造vo对象
     * @return ShareRetVo
     * @author Qiuyan Qian
     * @date Created in 2020/11/24 21:50
     */
    @Override
    public Object createVo(){
        return new ShareRetVo(this);
    }

    @Override
    public Object createSimpleVo(){
        return null;
    }
}
