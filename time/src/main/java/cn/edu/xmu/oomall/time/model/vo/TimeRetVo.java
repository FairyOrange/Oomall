package cn.edu.xmu.oomall.time.model.vo;

import cn.edu.xmu.oomall.time.model.bo.Time;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间返回Vo
 *
 * @author wwc
 * @date 2020/11/24 23:05
 * @version 1.0
 */
@Data
@ApiModel(description = "时间返回对象")
public class TimeRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "开始日期时间")
    private String beginTime;
    @ApiModelProperty(value = "结束日期时间")
    private String endTime;
    @ApiModelProperty(value = "建立时间")
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
    public TimeRetVo(Time bo) {
        this.setId(bo.getId());
        if (bo.getBeginTime() != null) {
            this.setBeginTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getBeginTime()));
        }
        if (bo.getEndTime() != null) {
            this.setEndTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getEndTime()));
        }
        if (bo.getGmtCreate() != null) {
            this.setGmtCreate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtCreate()));
        }
        if (bo.getGmtModified() != null) {
            this.setGmtModified(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtModified()));
        }
    }
}
