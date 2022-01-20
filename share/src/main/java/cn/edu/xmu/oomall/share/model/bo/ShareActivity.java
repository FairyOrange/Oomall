package cn.edu.xmu.oomall.share.model.bo;

import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.model.po.ShareActivityPo;
import cn.edu.xmu.oomall.share.model.vo.ShareActivityRetVo;
import cn.edu.xmu.oomall.share.model.vo.ShareActivityVo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fiber W.
 * created at 11/23/20 11:27 AM
 * @detail cn.edu.xmu.oomall.share.model.bo
 */
@Data
public class ShareActivity implements VoObject {
    private Long id;
    private Long shopId;
    private Long goodsSkuId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String strategy;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    private ShareActivityStrategy structStrategy;


    public enum State {
        ON(1, "上线"),
        OFF(0, "下线");
        private static final Map<Integer, ShareActivity.State> stateMap;

        static {
            // 由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (ShareActivity.State enumState : values()) {
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
         * 获取所有的枚举类型
         *
         * @return List 全部状态的List
         * @author wwc
         * @date 2020/11/23 18:57
         * @version 1.0
         */
        public static List getAllState() {
            List allState = Lists.newArrayList();
            for (ShareActivity.State enumType : values()) {
                allState.add(ImmutableMap.<String, Object> builder()
                        .put("code", enumType.code)
                        .put("name", enumType.description)
                        .build());
            }
            return allState;
        }

        public static State getStateByCode(int code) {
            return State.stateMap.get(code);
        }

        public int getCode() {
            return code;
        }
    }

    private State state;

    /**
     * @description 创建po
     * @return cn.edu.xmu.oomall.share.model.po.ShareActivityPo
     * @author Fiber W.
     * created at 11/23/20 1:07 PM
     */
    public ShareActivityPo createPo() {
        ShareActivityPo shareActivityPo = new ShareActivityPo();

        shareActivityPo.setId(this.id);
        shareActivityPo.setShopId(this.shopId);
        shareActivityPo.setGoodsSkuId(this.goodsSkuId);
        shareActivityPo.setBeginTime(this.beginTime);
        shareActivityPo.setEndTime(this.endTime);
        shareActivityPo.setStrategy(this.strategy);
        shareActivityPo.setGmtCreate(this.gmtCreate);
        shareActivityPo.setGmtModified(this.gmtModified);
        shareActivityPo.setState(Byte.valueOf((byte) this.state.code));

        return shareActivityPo;
    }

    public ShareActivity() {}

    /**
     * @description 构造函数 根据po对象生成bo
     * @param shareActivityPo po对象
     * @author Fiber W.
     * created at 11/23/20 1:07 PM
     */
    public ShareActivity(ShareActivityPo shareActivityPo) {
        this.id = shareActivityPo.getId();
        this.beginTime = shareActivityPo.getBeginTime();
        this.endTime = shareActivityPo.getEndTime();
        this.gmtCreate = shareActivityPo.getGmtCreate();
        this.gmtModified = shareActivityPo.getGmtModified();
        this.goodsSkuId = shareActivityPo.getGoodsSkuId();
        this.strategy = shareActivityPo.getStrategy();
        this.shopId = shareActivityPo.getShopId();
        this.state = shareActivityPo.getState() == null ? null : State.getStateByCode(shareActivityPo.getState());

        this.structStrategy = shareActivityPo.getStrategy() == null ? null : new ShareActivityStrategy(shareActivityPo.getStrategy());
    }

    /**
     * 根据vo生成bo
     * @param shareActivityVo
     * @author Fiber W.
     * created at 12/2/20 11:32 AM
     */
    public ShareActivity(ShareActivityVo shareActivityVo, Long shopId, Long goodsSkuId) {
        this.beginTime = Timestamp.valueOf(shareActivityVo.getBeginTime()).toLocalDateTime();
        this.endTime = Timestamp.valueOf(shareActivityVo.getEndTime()).toLocalDateTime();
        this.shopId = shopId;
        this.goodsSkuId = goodsSkuId;
        this.strategy = shareActivityVo.getStrategy();
        this.gmtCreate = LocalDateTime.now();
        this.state = State.OFF;


    }

    /**
     * 检查分享活动是否正确
     * @author Fiber W.
     * created at 12/2/20 11:46 AM
     */
    public boolean check() {
        this.structStrategy = new ShareActivityStrategy(this.getStrategy());

        return this.structStrategy.check();
    }

    public boolean checkTime() {
        if (this.beginTime.isAfter(this.endTime)) {
            return false;
        }
        return true;
    }

    /**
     * @description 用bo对象创建vo对象
     * @auther  Qiuyan Qian
     * @date  Created in 2020/11/25 下午9:07
    */
    @Override
    public ShareActivityRetVo createVo(){ return new ShareActivityRetVo(this); }

    @Override
    public Object createSimpleVo(){ return null; }
}
