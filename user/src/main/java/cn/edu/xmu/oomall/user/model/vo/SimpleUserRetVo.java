package cn.edu.xmu.oomall.user.model.vo;


import cn.edu.xmu.oomall.user.model.bo.SimpleUser;
import cn.edu.xmu.oomall.user.model.bo.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * 简单用户返回vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
@ApiModel(description = "用户返回对象")
public class SimpleUserRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "昵称")
    private String userName;
    @ApiModelProperty(value = "真实姓名")
    private String realName;

    /**
     * 通过bo构造
     *
     * @author wwc
     * @date 2020/11/25 14:13
     * @version 1.0
     */
    public SimpleUserRetVo(SimpleUser bo) {
        this.setId(bo.getId());
        this.setUserName(bo.getUserName());
        this.setRealName(bo.getRealName());
    }
}
