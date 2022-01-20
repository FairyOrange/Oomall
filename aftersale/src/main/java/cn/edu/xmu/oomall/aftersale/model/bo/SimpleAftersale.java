package cn.edu.xmu.oomall.aftersale.model.bo;

import cn.edu.xmu.oomall.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.oomall.aftersale.model.vo.AftersaleRetVo;
import cn.edu.xmu.oomall.aftersale.model.vo.AftersaleVo;
import cn.edu.xmu.oomall.aftersale.model.vo.SimpleAftersaleRetVo;
import cn.edu.xmu.oomall.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 售后简单Bo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/21 20:52
 */
@Data
public class SimpleAftersale implements VoObject {
    private Long id;
    private Long orderItemId;
    private Long customerId;
    private Long shopId;
    private String serviceSn;
    private Aftersale.Type type;
    private String reason;
    private Long refund;
    private Integer quantity;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Aftersale.State state;

    /**
     * po属性之外的属性
     *
     * @author wwc
     * @date 2020/11/25 17:02
     * @version 1.0
     */
    private Long orderId;
    private List orderItemIdList;

    public SimpleAftersale() {
    }

    /**
     * 通过vo构造bo
     *
     * @param vo vo对象
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    public SimpleAftersale(AftersaleVo vo) {
        if (vo.getType() != null) {
            this.setType(Aftersale.Type.getTypeByCode(vo.getType().intValue()));
        }
        this.setQuantity(vo.getQuantity());
        this.setReason(vo.getReason());
        this.setRegionId(vo.getRegionId());
        this.setDetail(vo.getDetail());
        this.setConsignee(vo.getConsignee());
        this.setMobile(vo.getMobile());
    }

    /**
     * 通过po构造bo
     *
     * @param po po对象
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    public SimpleAftersale(AftersaleServicePo po) {
        this.setId(po.getId());
        this.setOrderItemId(po.getOrderItemId());
        this.setCustomerId(po.getCustomerId());
        this.setShopId(po.getShopId());
        this.setServiceSn(po.getServiceSn());
        if (po.getType() != null) {
            this.setType(Aftersale.Type.getTypeByCode(po.getType().intValue()));
        }
        this.setReason(po.getReason());
        this.setRefund(po.getRefund());
        this.setQuantity(po.getQuantity());
        this.setRegionId(po.getRegionId());
        this.setDetail(po.getDetail());
        this.setConsignee(po.getConsignee());
        this.setMobile(po.getMobile());
        this.setCustomerLogSn(po.getCustomerLogSn());
        this.setShopLogSn(po.getShopLogSn());
        if (po.getState() != null) {
            this.setState(Aftersale.State.getTypeByCode(po.getState().intValue()));
        }
    }

    /**
     * 构造po对象
     *
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    public AftersaleServicePo creatPo() {
        AftersaleServicePo po = new AftersaleServicePo();
        po.setId(this.getId());
        po.setOrderItemId(this.getOrderItemId());
        po.setCustomerId(this.getCustomerId());
        po.setShopId(this.getShopId());
        po.setServiceSn(this.getServiceSn());
        if (this.getType() != null) {
            po.setType(this.getType().getCode().byteValue());
        }
        po.setReason(this.getReason());
        po.setRefund(this.getRefund());
        po.setQuantity(this.getQuantity());
        po.setRegionId(this.getRegionId());
        po.setDetail(this.getDetail());
        po.setConsignee(this.getConsignee());
        po.setMobile(this.getMobile());
        po.setCustomerLogSn(this.getCustomerLogSn());
        po.setShopLogSn(this.getShopLogSn());
        if (this.getState() != null) {
            po.setState(this.getState().getCode().byteValue());
        }
        return po;
    }

    /**
     * 构造vo对象
     *
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    @Override
    public Object createVo() {
        SimpleAftersaleRetVo vo = new SimpleAftersaleRetVo(this);
        return vo;
    }

    /**
     * 构造简单vo对象
     *
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    @Override
    public Object createSimpleVo() {
        SimpleAftersaleRetVo vo = new SimpleAftersaleRetVo(this);
        return vo;
    }
}
