package cn.edu.xmu.oomall.user.model.bo;

import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.user.model.po.CustomerPo;
import cn.edu.xmu.oomall.user.model.vo.UserModifyInfoVo;
import cn.edu.xmu.oomall.user.model.vo.UserRetVo;
import cn.edu.xmu.oomall.user.model.vo.UserVo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户bo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/28 23:14
 */
@Data
public class User implements VoObject {
    private Long id;
    private String userName;
    private String password;
    private String realName;
    private Gender gender;
    private LocalDate birthday;
    private Integer point;
    private State state;
    private String email;
    private String mobile;
    private Byte beDeleted;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public enum Gender {
        MAN(0, "男性"),
        WOMAN(1, "女性");

        private static final Map<Integer, User.Gender> stateMap;

        static {
            // 由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (User.Gender enumGender : values()) {
                stateMap.put(enumGender.code, enumGender);
            }
        }

        private int code;
        private String description;

        Gender(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 通过code获取枚举类型
         *
         * @author wwc
         * @date 2020/11/23 18:56
         * @version 1.0
         */
        public static User.Gender getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        /**
         * 获取所有的枚举类型
         *
         * @return List 全部状态的List
         * @author wwc
         * @date 2020/11/23 18:57
         * @version 1.0
         */
        public static List getAllType() {
            Map<User.Gender, Map<String, String>> allGenderMap = new HashMap();
            for (User.Gender enumGender : values()) {
                allGenderMap.put(enumGender, ImmutableMap.<String, String>builder()
                        .put("code", String.valueOf(enumGender.code))
                        .put("name", enumGender.getDescription())
                        .build());
            }
            return Lists.newArrayList(allGenderMap);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum State {
        ADMIN(0, "后台用户"),
        NORMAL(4, "正常用户"),
        BANED(6, "被封禁用户");

        private static final Map<Integer, User.State> stateMap;

        static {
            // 由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (User.State enumState : values()) {
                stateMap.put(enumState.code, enumState);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 通过code获取枚举类型
         *
         * @author wwc
         * @date 2020/11/23 18:56
         * @version 1.0
         */
        public static User.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        /**
         * 获取所有的枚举类型
         *
         * @return List 全部状态的List
         * @author wwc
         * @date 2020/11/23 18:57
         * @version 1.0
         */
        public static List getAllType() {

            List allState = Lists.newArrayList();
            for (User.State enumType : values()) {
                allState.add(ImmutableMap.<String, Object> builder()
                        .put("code", enumType.getCode())
                        .put("name", enumType.getDescription())
                        .build());
            }
            return allState;

        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * po属性之外的属性
     *
     * @author wwc
     * @date 2020/11/25 17:02
     * @version 1.0
     */

    public User() {
    }

    /**
     * 通过vo构造bo
     *
     * @param vo vo对象
     * @author wwc
     * @date 2020/11/24 23:17
     * @version 1.0
     */
    public User(UserVo vo) {
        this.setMobile(vo.getMobile());
        this.setEmail(vo.getEmail());
        this.setUserName(vo.getUserName());
        this.setPassword(vo.getPassword());
        this.setRealName(vo.getRealName());
        this.setGender(User.Gender.getTypeByCode(vo.getGender().intValue()));
        this.setBirthday(vo.getBirthday());
    }

    /**
     * 通过vo构造bo
     *
     * @param vo vo对象
     * @author wwc
     * @date 2020/11/24 23:17
     * @version 1.0
     */
    public User(UserModifyInfoVo vo) {
        this.setRealName(vo.getRealName());
        this.setGender(User.Gender.getTypeByCode(vo.getGender().intValue()));
        this.setBirthday(vo.getBirthday());
    }

    /**
     * 通过po构造bo
     *
     * @param po po对象
     * @author wwc
     * @date 2020/11/24 23:17
     * @version 1.0
     */
    public User(CustomerPo po) {
        this.setId(po.getId());
        this.setUserName(po.getUserName());
        this.setPassword(po.getPassword());
        this.setRealName(po.getRealName());
        if (po.getGender() != null) {
            this.setGender(User.Gender.getTypeByCode(po.getGender().intValue()));
        }
        this.setBirthday(po.getBirthday());
        this.setPoint(po.getPoint());
        if (po.getState() != null) {
            this.setState(User.State.getTypeByCode(po.getState().intValue()));
        }
        this.setEmail(po.getEmail());
        this.setMobile(po.getMobile());
        this.setBeDeleted(po.getBeDeleted());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }

    /**
     * 构造po对象
     *
     * @author wwc
     * @date 2020/11/24 23:15
     * @version 1.0
     */
    public CustomerPo createPo() {
        CustomerPo po = new CustomerPo();
        po.setId(this.getId());
        po.setUserName(this.getUserName());
        po.setPassword(this.getPassword());
        po.setRealName(this.getRealName());
        if (this.getGender() != null) {
            po.setGender(this.getGender().getCode().byteValue());
        }
        po.setBirthday(this.getBirthday());
        po.setPoint(this.getPoint());
        if (this.getState() != null) {
            po.setState(this.getState().getCode().byteValue());
        }
        po.setEmail(this.getEmail());
        po.setMobile(this.getMobile());
        po.setBeDeleted(this.getBeDeleted());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
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
        UserRetVo vo = new UserRetVo(this);
        return vo;
    }

    /**
     * 构造简单vo对象
     *
     * @author wwc
     * @date 2020/11/25s 18:58
     * @version 1.0
     */
    @Override
    public Object createSimpleVo() {
        UserRetVo vo = new UserRetVo(this);
        return vo;
    }
}
