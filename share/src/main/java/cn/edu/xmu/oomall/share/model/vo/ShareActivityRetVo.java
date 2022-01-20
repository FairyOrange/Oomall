package cn.edu.xmu.oomall.share.model.vo;

import cn.edu.xmu.oomall.share.model.bo.ShareActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享活动返回vo对象
 * @author Qiuyan Qian
 * @date Created in 2020/11/25 20:57
 */
@Data
@ApiModel(value = "分享活动返回对象")
public class ShareActivityRetVo {
    @ApiModelProperty(value = "分享活动id")
    private Long id;

    @ApiModelProperty(value = "商铺id")
    private Long shopId;

    @ApiModelProperty(value = "商品skuId")
    private Long skuId;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "状态")
    private Byte state;

    /**
     * 用bo构造vo
     * @author Qiuyan Qian
     * @date  Created in 2020/11/25 下午9:04
     */
    public ShareActivityRetVo(ShareActivity shareActivity){
        this.setId(shareActivity.getId());
        this.setShopId(shareActivity.getShopId());
        this.setSkuId(shareActivity.getGoodsSkuId());
        this.setBeginTime(shareActivity.getBeginTime().toString());
        this.setEndTime(shareActivity.getEndTime().toString());
        if (shareActivity.getState() != null) {
            this.setState((byte) shareActivity.getState().getCode());
        }

    }
}
