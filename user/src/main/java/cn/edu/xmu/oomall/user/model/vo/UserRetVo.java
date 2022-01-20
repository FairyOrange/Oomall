package cn.edu.xmu.oomall.user.model.vo;

import cn.edu.xmu.oomall.user.model.bo.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.format.DateTimeFormatter;

/**
 * 用户返回vo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
@ApiModel(description = "用户返回对象")
public class UserRetVo {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "昵称")
    private String userName;
    @ApiModelProperty(value = "真实姓名")
    private String name;
    @ApiModelProperty(value = "电话")
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "性别")
    private Byte gender;
    @ApiModelProperty(value = "生日")
    private String birthday;
    @ApiModelProperty(value = "状态")
    private Byte state;
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
    public UserRetVo(User bo) {
        this.setId(bo.getId());
        this.setUserName(bo.getUserName());
        this.setName(bo.getRealName());
        this.setMobile(bo.getMobile());
        this.setEmail(bo.getEmail());
        if (bo.getGender() != null) {
            this.setGender(bo.getGender().getCode().byteValue());
        }
        if (bo.getBirthday() != null) {
            this.setBirthday(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(bo.getBirthday()));
        }
        if (bo.getState() != null) {
            this.setState(bo.getState().getCode().byteValue());
        }
        if (bo.getGmtCreate() != null) {
            this.setGmtCreate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtCreate()));
        }
        if (bo.getGmtModified() != null) {
            this.setGmtModified(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(bo.getGmtModified()));
        }
    }
}
