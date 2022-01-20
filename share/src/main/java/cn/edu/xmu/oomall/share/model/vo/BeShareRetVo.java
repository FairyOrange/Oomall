package cn.edu.xmu.oomall.share.model.vo;

import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.share.model.bo.BeShare;
import cn.edu.xmu.oomall.share.model.bo.SkuInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享成功返回vo对象
 * @author Qiuyan Qian
 * @date Created in 2020/11/25 19:08
 */
@Data
@ApiModel(value = "分享成功返回对象")
public class BeShareRetVo {

    @ApiModelProperty(value = "分享成功记录id")
    private Long id;

    @ApiModelProperty(value = "商品sku")
    private SkuInfo sku;

    @ApiModelProperty(value = "分享者id")
    private Long sharerId;

    @ApiModelProperty(value = "被分享者id")
    private Long customerId;

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "返点")
    private Integer rebate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    /**
     * 用bo构造vo
     */
    public BeShareRetVo(BeShare beShare){
        this.setId(beShare.getId());
        //this.setGoodsSkuId(beShare.getGoodsSkuId());
        this.setSku(beShare.getSku());
        this.setSharerId(beShare.getSharerId());
        this.setCustomerId(beShare.getCustomerId());
        this.setOrderId(beShare.getOrderId());
        this.setRebate(beShare.getRebate());
        this.setGmtCreate(beShare.getGmtCreate());
    }

}
