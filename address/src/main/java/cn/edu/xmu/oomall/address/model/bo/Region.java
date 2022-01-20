package cn.edu.xmu.oomall.address.model.bo;

import cn.edu.xmu.oomall.address.model.po.RegionPo;
import cn.edu.xmu.oomall.address.model.vo.RegionRetVo;
import cn.edu.xmu.oomall.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地区Bo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:08
 */
@Data
public class Region implements VoObject, Serializable {
    private Long id;
    private Long pid;
    private String name;
    private Long postalCode;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Region() {
    }

    /**
     * @param name       地区名
     * @param postalCode 邮政编码邮编
     * @author wwc
     * @date 2020/11/24 23:24
     * @version 1.0
     */
    public Region(String name, Long postalCode) {
        this.setName(name);
        this.setPostalCode(postalCode);
    }

    /**
     * 通过po构造bo
     *
     * @param po po对象
     * @author wwc
     * @date 2020/11/24 23:23
     * @version 1.0
     */
    public Region(RegionPo po) {
        this.setId(po.getId());
        this.setPid(po.getPid());
        this.setName(po.getName());
        this.setPostalCode(po.getPostalCode());
        this.setState(po.getState());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }

    /**
     * 构造po对象
     *
     * @author wwc
     * @date 2020/11/24 23:43
     * @version 1.0
     */
    public RegionPo createPo() {
        RegionPo po = new RegionPo();
        po.setId(this.getId());
        po.setPid(this.getPid());
        po.setName(this.getName());
        po.setPostalCode(this.getPostalCode());
        po.setState(this.getState());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }

    /**
     * 构造vo对象
     *
     * @author wwc
     * @date 2020/11/25 18:58
     * @version 1.0
     */
    @Override
    public Object createVo() {
        RegionRetVo vo = new RegionRetVo(this);
        return vo;
    }

    /**
     * 构造简单vo对象
     *
     * @author wwc
     * @date 2020/11/25 18:58
     * @version 1.0
     */
    @Override
    public Object createSimpleVo() {
        RegionRetVo vo = new RegionRetVo(this);
        return vo;
    }
}
