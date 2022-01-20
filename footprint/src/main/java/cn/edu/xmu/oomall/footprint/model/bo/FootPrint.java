package cn.edu.xmu.oomall.footprint.model.bo;


import cn.edu.xmu.oomall.footprint.model.po.FootPrintPo;
import cn.edu.xmu.oomall.footprint.model.vo.FootPrintRetVo;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.model.VoObject;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 足迹Bo
 *
 * @author yang8miao
 * @date 2020/11/26 20:02
 * @version 1.0
 */
@Data
public class FootPrint implements VoObject {

    private Long id;

    private Long customerId;

    private Long goodsSkuId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private SkuInfo goodsSku;

    public FootPrint() {}


    public FootPrintPo createPo(){
        FootPrintPo po = new FootPrintPo();
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
     * @date 2020/11/26 20:56
     * @version 1.0
     */
    public FootPrint(FootPrintPo po) {
        this.setId(po.getId());
        this.setCustomerId(po.getCustomerId());
        this.setGoodsSkuId(po.getGoodsSkuId());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }

    /**
     * 用bo对象创造vo对象
     * @return FootPrintRetVo
     * @author yang8miao
     * @date Created in 2020/11/26 23:07
     */
    @Override
    public Object createVo() { return new FootPrintRetVo(this);}

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
