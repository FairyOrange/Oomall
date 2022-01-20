package cn.edu.xmu.oomall.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/12/15 22:08
 */
@Data
@ApiModel(description = "维修寄回运单号接受对象")
public class ShopLogSnVo {
    @ApiModelProperty(value = "维修寄回运单号")
    private String shopLogSn;
}
