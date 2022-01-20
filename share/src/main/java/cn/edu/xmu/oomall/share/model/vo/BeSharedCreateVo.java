package cn.edu.xmu.oomall.share.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 生成分享成功状态参数vo对象
 * @author Qiuyan Qian
 * @date Created in 2020/12/01 11:41
 */
@Data
@ApiModel(value = "生成分享成功状态参数队形")
public class BeSharedCreateVo {

    @ApiModelProperty(value = "分享记录Id")
    @NotNull(message = "分享记录id不能为空")
    private Long shareId;
}
