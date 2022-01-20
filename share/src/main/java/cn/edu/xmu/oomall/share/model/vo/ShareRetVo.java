package cn.edu.xmu.oomall.share.model.vo;

import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.share.model.bo.Share;
import cn.edu.xmu.oomall.share.model.bo.SkuInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享返回vo对象
 * @author Qiuyan Qian
 * @date Created in 2020/11/24 22:43
 */
@Data
@ApiModel(description = "分享返回对象")
public class ShareRetVo {
    @ApiModelProperty(value = "分享记录id")
    private Long id;

    @ApiModelProperty(value = "分享者id")
    private Long sharerId;

    @ApiModelProperty(value = "商品sku")
    private SkuInfo sku;

    @ApiModelProperty(value = "分享数量")
    private Integer quantity;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    /**
     * 用bo构造vo
     * @param share
     */
    public ShareRetVo(Share share){
        this.setId(share.getId());
        this.setSharerId(share.getSharerId());
        this.setSku(share.getSku());
        this.setQuantity(share.getQuantity());
        this.setGmtCreate(share.getGmtCreate());
    }
}
