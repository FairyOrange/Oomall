package cn.edu.xmu.oomall.share.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Fiber W.
 * created at 12/4/20 3:04 PM
 * @detail cn.edu.xmu.oomall.share.model.vo
 */
@Data
public class ShareVo {

    @ApiModelProperty(value = "商品spuId")
    private Long goodsSkuId;
}
