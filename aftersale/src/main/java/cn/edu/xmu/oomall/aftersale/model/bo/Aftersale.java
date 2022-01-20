package cn.edu.xmu.oomall.aftersale.model.bo;

import cn.edu.xmu.oomall.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.oomall.aftersale.model.vo.AftersaleRetVo;
import cn.edu.xmu.oomall.aftersale.model.vo.AftersaleUpdateVo;
import cn.edu.xmu.oomall.aftersale.model.vo.AftersaleVo;
import cn.edu.xmu.oomall.model.VoObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后Bo
 *
 * @author wwc
 * @version 1.0
 * @date 2020/11/21 20:52
 */
@Data
public class Aftersale implements VoObject {
    private Long id;
    private Long orderItemId;
    private Long customerId;
    private Long shopId;
    private String serviceSn;
    private Type type;
    private String reason;
    private String conclusion;
    private Long refund;
    private Integer quantity;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Byte beDeleted;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private State state;

    /**
     * po属性之外的属性
     *
     * @author wwc
     * @date 2020/11/25 17:02
     * @version 1.0
     */
    private Long orderId;
    private String orderSn;
    private Long spuId;
    private Long skuId;
    private String skuName;

    public enum Type {
        EXCHANGE(0, "换货"),
        RETURN_BACK(1, "退货"),
        REPAIR(2, "维修");

        private static final Map<Integer, Aftersale.Type> stateMap;

        static {
            // 由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Aftersale.Type enumType : values()) {
                stateMap.put(enumType.code, enumType);
            }
        }

        private int code;
        private String description;

        Type(int code, String description) {
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
        public static Aftersale.Type getTypeByCode(Integer code) {
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
            List allType = Lists.newArrayList();
            for (Aftersale.Type enumType : values()) {
                allType.add(ImmutableMap.<String, Object> builder()
                        .put("code", enumType.code)
                        .put("name", enumType.getDescription())
                        .build());
            }
            return allType;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum State {
        WAITING_SHOP_REVIEW(0, "待管理员审核"),
        WAITING_USER_DELIVE(1, "待买家发货"),
        USER_DELIVE(2, "买家已发货"),
        WAITING_SHOP_REFUND(3, "待店家退款"),
        WAITING_SHOP_DELIVE(4, "待店家发货"),
        SHOP_DELIVE(5, "店家已发货"),
        SHOP_REFUSE(6, "审核不通过"),
        CANCEL(7, "已取消"),
        FINISH(8, "已结束");

        private static final Map<Integer, Aftersale.State> stateMap;

        static {
            // 由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Aftersale.State enumState : values()) {
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
        public static Aftersale.State getTypeByCode(Integer code) {
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
        public static List getAllState() {
            List allState = Lists.newArrayList();
            for (Aftersale.State enumType : values()) {
                allState.add(ImmutableMap.<String, Object> builder()
                        .put("code", enumType.code)
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

    public Aftersale() {
    }

    /**
     * 通过vo构造bo
     *
     * @param vo vo对象
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    public Aftersale(AftersaleVo vo) {
        if (vo.getType() != null) {
            this.setType(Aftersale.Type.getTypeByCode(vo.getType().intValue()));
        }
        this.setQuantity(vo.getQuantity());
        this.setReason(vo.getReason());
        this.setRegionId(vo.getRegionId());
        this.setDetail(vo.getDetail());
        this.setConsignee(vo.getConsignee());
        this.setMobile(vo.getMobile());
    }

    /**
     * 通过修改vo构造bo
     *
     * @param vo vo对象
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    public Aftersale(AftersaleUpdateVo vo) {
        this.setQuantity(vo.getQuantity());
        this.setReason(vo.getReason());
        this.setRegionId(vo.getRegionId());
        this.setDetail(vo.getDetail());
        this.setConsignee(vo.getConsignee());
        this.setMobile(vo.getMobile());
    }

    /**
     * 通过po构造bo
     *
     * @param po po对象
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    public Aftersale(AftersaleServicePo po) {
        this.setId(po.getId());
        this.setOrderItemId(po.getOrderItemId());
        this.setCustomerId(po.getCustomerId());
        this.setShopId(po.getShopId());
        this.setServiceSn(po.getServiceSn());
        if (po.getType() != null) {
            this.setType(Aftersale.Type.getTypeByCode(po.getType().intValue()));
        }
        this.setReason(po.getReason());
        this.setConclusion(po.getConclusion());
        this.setRefund(po.getRefund());
        this.setQuantity(po.getQuantity());
        this.setRegionId(po.getRegionId());
        this.setDetail(po.getDetail());
        this.setConsignee(po.getConsignee());
        this.setMobile(po.getMobile());
        this.setCustomerLogSn(po.getCustomerLogSn());
        this.setShopLogSn(po.getShopLogSn());
        if (po.getState() != null) {
            this.setState(State.getTypeByCode(po.getState().intValue()));
        }
        this.setBeDeleted(po.getBeDeleted());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
        this.setOrderId(po.getOrderId());
    }

    /**
     * 构造po对象
     *
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    public AftersaleServicePo creatPo() {
        AftersaleServicePo po = new AftersaleServicePo();
        po.setId(this.getId());
        po.setOrderItemId(this.getOrderItemId());
        po.setCustomerId(this.getCustomerId());
        po.setShopId(this.getShopId());
        po.setServiceSn(this.getServiceSn());
        if (this.getType() != null) {
            po.setType(this.getType().getCode().byteValue());
        }
        po.setReason(this.getReason());
        po.setConclusion(this.getConclusion());
        po.setRefund(this.getRefund());
        po.setQuantity(this.getQuantity());
        po.setRegionId(this.getRegionId());
        po.setDetail(this.getDetail());
        po.setConsignee(this.getConsignee());
        po.setMobile(this.getMobile());
        po.setCustomerLogSn(this.getCustomerLogSn());
        po.setShopLogSn(this.getShopLogSn());
        if (this.getState() != null) {
            po.setState(this.getState().getCode().byteValue());
        }
        po.setBeDeleted(this.getBeDeleted());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        po.setOrderId(this.getOrderId());
        return po;
    }

    /**
     * 构造vo对象
     *
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    @Override
    public Object createVo() {
        AftersaleRetVo vo = new AftersaleRetVo(this);
        return vo;
    }

    /**
     * 构造简单vo对象
     *
     * @author wwc
     * @date 2020/11/23 18:58
     * @version 1.0
     */
    @Override
    public Object createSimpleVo() {
        AftersaleRetVo vo = new AftersaleRetVo(this);
        return vo;
    }
}
