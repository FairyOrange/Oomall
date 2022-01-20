package cn.edu.xmu.oomall.advertise.model.vo;

import cn.edu.xmu.oomall.advertise.model.bo.AdvertiseBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author:xiaoru chen
 * @date 2020/11/25 13：02
 */
@Data
@ApiModel(description = "广告接受信息对象")
public class AdvertiseVo {

    @ApiModelProperty(value = "内容")
    @NotEmpty(message = "广告内容不能为空")
    private String content;

    @ApiModelProperty(value = "开始日期")
    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate beginDate;

    @ApiModelProperty(value = "结束日期")
    @NotNull(message = "结束日期不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @ApiModelProperty(value = "排序权重")
    @Min(value = 1, message = "排序权重不能小于1")
    @NotNull(message = "排序权重不能为空")
    private Integer weight;

    @ApiModelProperty(value = "是否为每日重复广告")
    @NotNull(message = "是否为每日重复广告不能为空")
    private Boolean repeat;

    @ApiModelProperty(value = "广告链接")
    @NotEmpty(message = "广告链接不能为空")
    private String link;
}
