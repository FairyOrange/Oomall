package cn.edu.xmu.oomall.share.model.bo;

import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.model.po.BeSharePo;
import cn.edu.xmu.oomall.share.model.vo.BeShareRetVo;
import cn.edu.xmu.oomall.share.model.vo.ShareRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Fiber W.
 * created at 11/23/20 9:32 AM
 * @detail cn.edu.xmu.oomall.share.model.bo
 */
@Data
public class BeShare implements Comparable<BeShare>, VoObject {

    private Long id;
    private Long goodsSkuId;
    private Long sharerId;
    private Long shareId;
    private Long customerId;
    private Long orderId;
    private Integer rebate;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long shareActivityId;
    private SkuInfo sku;

    public BeShare() {}

    /**
     * 使用PO构BO
     * @param po OtherBeSharePo对象
     * @author Fiber W.
     * created at 11/23/20 9:38 AM
     */
    public BeShare(BeSharePo po) {
        this.id = po.getId();
        this.customerId = po.getCustomerId();
        this.goodsSkuId = po.getGoodsSkuId();
        this.orderId = po.getOrderId();
        this.rebate = po.getRebate();
        this.shareId = po.getShareId();
        this.sharerId = po.getSharerId();

        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.shareActivityId = po.getShareActivityId();
    }

    /**
     * @description 构造PO
     * @return cn.edu.xmu.oomall.share.model.po.OtherBeSharePo
     * @author Fiber W.
     * created at 11/23/20 9:37 AM
     */
    public BeSharePo createPo() {
        BeSharePo beSharePo = new BeSharePo();

        beSharePo.setId(this.id);
        beSharePo.setCustomerId(this.customerId);
        beSharePo.setGoodsSkuId(this.goodsSkuId);
        beSharePo.setOrderId(this.orderId);
        beSharePo.setRebate(this.rebate);
        beSharePo.setShareId(this.shareId);
        beSharePo.setSharerId(this.sharerId);
        beSharePo.setGmtCreate(this.gmtCreate);
        beSharePo.setGmtModified(this.gmtModified);
        beSharePo.setShareActivityId(this.shareActivityId);

        return beSharePo;
    }

    /**
     * @description 排序比较函数，按照gmtCreated排序
     * @param o 对象
     * @return int
     * @author Fiber W.
     * created at 11/24/20 2:47 PM
     */
    @Override
    public int compareTo(BeShare o) {
        return this.gmtCreate.compareTo(o.gmtCreate);
    }

    /**
     * @description 用bo对象创造vo对象
     * @return
     */
    @Override
    public Object createVo(){ return new BeShareRetVo(this); }

    @Override
    public Object createSimpleVo(){ return null; }
}
