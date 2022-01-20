package cn.edu.xmu.oomall.aftersale.model.vo;

import cn.edu.xmu.oomall.aftersale.model.bo.SimpleAftersale;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后retVo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/21 20:54
 */
@Data
@ApiModel(description = "售后返回对象")
public class SimpleAftersaleRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "订单id")
    private Long orderId;
    @ApiModelProperty(value = "订单物品id")
    private Long orderItemId;
    @ApiModelProperty(value = "顾客id")
    private Long customerId;
    @ApiModelProperty(value = "店铺id")
    private Long shopId;
    @ApiModelProperty(value = "售后单序号")
    private String serviceSn;
    @ApiModelProperty(value = "服务类型")
    private Integer type;
    @ApiModelProperty(value = "原因")
    private String reason;
    @ApiModelProperty(value = "金额")
    private Long refund;
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    @ApiModelProperty(value = "地区码")
    private Long regionId;
    @ApiModelProperty(value = "详细地址")
    private String detail;
    @ApiModelProperty(value = "联系人")
    private String consignee;
    @ApiModelProperty(value = "电话")
    private String mobile;
    @ApiModelProperty(value = "寄出运单号")
    private String customerLogSn;
    @ApiModelProperty(value = "寄回运单号")
    private String shopLogSn;
//    @ApiModelProperty(value = "建立时间")
//    private String gmtCreate;
//    @ApiModelProperty(value = "修改时间")
//    private String gmtModified;
    @ApiModelProperty(value = "售后状态")
    private Byte state;

    /**
     * 通过bo构造
     *
     * @author wwc
     * @date 2020/11/25 14:13
     * @version 1.0
     */
    public SimpleAftersaleRetVo(SimpleAftersale bo) {
        this.setId(bo.getId());
        this.setOrderId(bo.getOrderId());
        this.setOrderItemId(bo.getOrderItemId());
        this.setCustomerId(bo.getCustomerId());
        this.setShopId(bo.getShopId());
        this.setServiceSn(bo.getServiceSn());
        if (bo.getType() != null) {
            this.setType(bo.getType().getCode());
        }
        this.setReason(bo.getReason());
        this.setRefund(bo.getRefund());
        this.setQuantity(bo.getQuantity());
        this.setRegionId(bo.getRegionId());
        this.setDetail(bo.getDetail());
        this.setConsignee(bo.getConsignee());
        this.setMobile(bo.getMobile());
        this.setCustomerLogSn(bo.getCustomerLogSn());
        this.setShopLogSn(bo.getShopLogSn());
        if (bo.getState() != null) {
            this.setState(bo.getState().getCode().byteValue());
        }
//        if (bo.getGmtCreate() != null) {
//            this.setGmtCreate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtCreate()));
//        }
//        if (bo.getGmtModified() != null) {
//            this.setGmtModified(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtModified()));
//        }
    }
}
