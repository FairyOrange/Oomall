package cn.edu.xmu.oomall.footprint.model.vo;


import cn.edu.xmu.oomall.footprint.model.bo.FootPrint;
import cn.edu.xmu.oomall.footprint.model.bo.SkuInfo;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 足迹返回vo对象
 * @author yang8miao
 * @date Created in 2020/11/26 23:08
 */
@Data
@ApiModel(description = "足迹返回对象")
public class FootPrintRetVo {

    @ApiModelProperty(value = "足迹id")
    private Long id;

    @ApiModelProperty(value = "商品sku")
    private SkuInfo goodsSku;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    /**
     * 用bo构造vo
     * @param footPrint
     */
    public FootPrintRetVo(FootPrint footPrint){
        this.setId(footPrint.getId());
        this.setGoodsSku(footPrint.getGoodsSku());
        this.setGmtCreate(footPrint.getGmtCreate());
    }
}
