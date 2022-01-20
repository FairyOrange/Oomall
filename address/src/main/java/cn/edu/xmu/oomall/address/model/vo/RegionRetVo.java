package cn.edu.xmu.oomall.address.model.vo;

import cn.edu.xmu.oomall.address.model.bo.Region;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * 地区返回vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/25 14:08
 */
@Data
@ApiModel(description = "地区返回对象")
public class RegionRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "上级地区id")
    private Long pid;
    @ApiModelProperty(value = "地区名称")
    private String name;
    @ApiModelProperty(value = "邮政编码")
    private Long postalCode;
    @ApiModelProperty(value = "状态")
    private Byte state;
    @ApiModelProperty(value = "创建时间")
    private String gmtCreate;
    @ApiModelProperty(value = "修改时间")
    private String gmtModified;

    /**
     * 通过bo构造
     *
     * @author wwc
     * @date 2020/11/25 14:13
     * @version 1.0
     */
    public RegionRetVo(Region bo) {
        this.setId(bo.getId());
        this.setPid(bo.getPid());
        this.setName(bo.getName());
        this.setPostalCode(bo.getPostalCode());
        this.setState(bo.getState());
        if (bo.getGmtCreate() != null) {
            this.setGmtCreate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtCreate()));
        }
        if (bo.getGmtModified() != null) {
            this.setGmtModified(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtModified()));
        }
    }
}
