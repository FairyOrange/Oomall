package cn.edu.xmu.oomall.user.model.bo;

import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.user.model.po.CustomerPo;
import cn.edu.xmu.oomall.user.model.vo.SimpleUserRetVo;
import cn.edu.xmu.oomall.user.model.vo.UserModifyInfoVo;
import cn.edu.xmu.oomall.user.model.vo.UserRetVo;
import cn.edu.xmu.oomall.user.model.vo.UserVo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单用户bo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
public class SimpleUser implements VoObject {
    private Long id;
    private String userName;
    private String realName;

    public SimpleUser() {
    }

    /**
     * 通过po构造bo
     *
     * @param po po对象
     * @author wwc
     * @date 2020/11/24 23:17
     * @version 1.0
     */
    public SimpleUser(CustomerPo po) {
        this.setId(po.getId());
        this.setUserName(po.getUserName());
        this.setRealName(po.getRealName());
    }

    /**
     * 构造vo对象
     *
     * @author wwc
     * @date 2020/11/25 18:58
     * @version 1.0
     */
    @Override
    public Object createVo() {
        SimpleUserRetVo vo = new SimpleUserRetVo(this);
        return vo;
    }

    /**
     * 构造简单vo对象
     *
     * @author wwc
     * @date 2020/11/25 18:58
     * @version 1.0
     */
    @Override
    public Object createSimpleVo() {
        SimpleUserRetVo vo = new SimpleUserRetVo(this);
        return vo;
    }
}
