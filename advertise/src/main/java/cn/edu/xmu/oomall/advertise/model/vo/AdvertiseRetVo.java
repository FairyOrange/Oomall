package cn.edu.xmu.oomall.advertise.model.vo;

import cn.edu.xmu.oomall.advertise.model.bo.AdvertiseBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@ApiModel(description = "广告返回对象")
public class AdvertiseRetVo {

    @ApiModelProperty(name = "主键")
    private Long id;
    @ApiModelProperty(name = "广告链接")
    private String link;
    @ApiModelProperty(name = "图片链接")
    private String imagePath;
    @ApiModelProperty(name = "内容")
    private String content;
    @ApiModelProperty(name = "广告时段id")
    private Long segId;
    @ApiModelProperty(name = "广告状态")
    private Integer state;
    @ApiModelProperty(name = "排序权重")
    private String weight;
    @ApiModelProperty(name = "设为默认")
    private Boolean beDefault;
    @ApiModelProperty(name = "开始日期")
    private String beginDate;
    @ApiModelProperty(name = "结束日期")
    private String endDate;
    @ApiModelProperty(name = "是否为每日重复的广告")
    private Boolean repeat;
    @ApiModelProperty(name = "创建时间")
    private String gmtCreate;
    @ApiModelProperty(name = "修改时间")
    private String gmtModified;

    /**
     * 通过bo构造
     * @author cxr
     * @param bo
     */
    public AdvertiseRetVo(AdvertiseBo bo){
        this.setId(bo.getId());
        this.setLink(bo.getLink());
        this.setImagePath(bo.getImageUrl());
        this.setContent(bo.getContent());
        this.setSegId(bo.getSegId());
        if(bo.getState()!=null){
            this.setState(bo.getState().getCode());
        }
        if(bo.getWeight()!=null){
            this.setWeight(bo.getWeight().toString());
        }
        if(bo.getBeDefault()!=null) {
            this.setBeDefault(bo.getBeDefault().equals((byte) 1));
        }
        if (bo.getBeginDate() != null) {
            this.setBeginDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(bo.getBeginDate()));
        }
        if (bo.getEndDate() != null) {
            this.setEndDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(bo.getEndDate()));
        }
        if(bo.getRepeats() != null) {
            this.setRepeat(bo.getRepeats().equals((byte) 1));
        }
        if (bo.getGmtCreate() != null) {
            this.setGmtCreate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtCreate()));
        }
        if (bo.getGmtModified() != null) {
            this.setGmtModified(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtModified()));
        }
    }

}
