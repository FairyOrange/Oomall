package cn.edu.xmu.oomall.address.model.vo;

import cn.edu.xmu.oomall.address.model.bo.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * 地址返回Vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/24 23:05
 */
@Data
@ApiModel(description = "地址返回对象")
public class AddressRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "地区码")
    private Long regionId;
    @ApiModelProperty(value = "详细地址")
    private String detail;
    @ApiModelProperty(value = "联系人")
    private String consignee;
    @ApiModelProperty(value = "电话")
    private String mobile;
    @ApiModelProperty(value = "是否默认")
    private Boolean beDefault;
    @ApiModelProperty(value = "建立时间")
    private String gmtCreate;
    @ApiModelProperty(value = "修改时间")
    private String gmtModified;
    @ApiModelProperty(value = "状态")
    private Byte state;

    /**
     * 通过bo构造
     *
     * @author wwc
     * @date 2020/11/25 14:13
     * @version 1.0
     */
    public AddressRetVo(Address bo) {
        this.setId(bo.getId());
        this.setRegionId(bo.getRegionId());
        this.setDetail(bo.getDetail());
        this.setConsignee(bo.getConsignee());
        this.setMobile(bo.getMobile());
        this.setBeDefault(bo.getBeDefault() == 1 ? true : false);
        if (bo.getGmtCreate() != null) {
            this.setGmtCreate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtCreate()));
        }
        if (bo.getGmtModified() != null) {
            this.setGmtModified(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtModified()));
        }
        this.setState(bo.getState());
    }
}
