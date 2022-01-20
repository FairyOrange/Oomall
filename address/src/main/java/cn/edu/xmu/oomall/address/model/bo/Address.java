package cn.edu.xmu.oomall.address.model.bo;

import cn.edu.xmu.oomall.address.model.po.AddressPo;
import cn.edu.xmu.oomall.address.model.vo.AddressRetVo;
import cn.edu.xmu.oomall.address.model.vo.AddressVo;
import cn.edu.xmu.oomall.model.VoObject;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地址Bo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:08
 */
@Data
public class Address implements VoObject {
    private Long id;
    private Long customerId;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private Byte beDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    /**
     * po属性之外的属性
     *
     * @author wwc
     * @date 2020/11/25 17:02
     * @version 1.0
     */
    private Byte state;

    public Address() {
    }

    /**
     * 通过vo构造bo
     *
     * @param vo vo对象
     * @author wwc
     * @date 2020/11/24 23:17
     * @version 1.0
     */
    public Address(AddressVo vo) {
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
     * @date 2020/11/24 23:17
     * @version 1.0
     */
    public Address(AddressPo po) {
        this.setId(po.getId());
        this.setCustomerId(po.getCustomerId());
        this.setRegionId(po.getRegionId());
        this.setDetail(po.getDetail());
        this.setConsignee(po.getConsignee());
        this.setMobile(po.getMobile());
        this.setBeDefault(po.getBeDefault());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }

    /**
     * 构造po对象
     *
     * @author wwc
     * @date 2020/11/24 23:15
     * @version 1.0
     */
    public AddressPo createPo() {
        AddressPo po = new AddressPo();
        po.setId(this.getId());
        po.setCustomerId(this.getCustomerId());
        po.setRegionId(this.getRegionId());
        po.setDetail(this.getDetail());
        po.setConsignee(this.getConsignee());
        po.setMobile(this.getMobile());
        po.setBeDefault(this.getBeDefault());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
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
        AddressRetVo vo = new AddressRetVo(this);
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
        AddressRetVo vo = new AddressRetVo(this);
        return vo;
    }
}
