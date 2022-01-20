package cn.edu.xmu.oomall.advertise.model.bo;

import cn.edu.xmu.oomall.advertise.model.po.AdvertisementPo;
import cn.edu.xmu.oomall.advertise.model.vo.AdvertiseRetVo;
import cn.edu.xmu.oomall.advertise.model.vo.AdvertiseVo;
import cn.edu.xmu.oomall.model.VoObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 广告Bo
 *
 * @author xiaoru chen
 * @date 2020/11/25 15:17
 * @version 1.0
 */
@Data
public class AdvertiseBo implements VoObject, Serializable {

    private Long id;
    private Long segId;
    private String link;
    private String content;
    private String imageUrl;
    private State state;
    private Integer weight;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Byte repeats;
    private String message;
    private Byte beDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    /**
     * 广告状态
     */
    public enum State {
        AUDIT(0, "待审核"),
        ONSHELF(4, "上架"),
        OFFSHELF(6, "下架");

        private static final Map<Integer, AdvertiseBo.State> stateMap;

        static {
            //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (AdvertiseBo.State enumState : values()) {
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
         * @author cxr
         * @param code
         * @return
         */
        public static AdvertiseBo.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        /**
         * 获取所有的枚举类型
         * @return List 全部状态的List
         * @author cxr
         */
        public static List getAllType() {
            List allState = Lists.newArrayList();
            for (AdvertiseBo.State enumType : values()) {
                allState.add(ImmutableMap.<String, Object> builder()
                        .put("code", Integer.valueOf(enumType.code))
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


    public AdvertiseBo() {}

    /**
     * 构造函数,用po构造bo
     * @param po Po对象
     */
    public AdvertiseBo(AdvertisementPo po){
        this.setId(po.getId());
        this.setSegId(po.getSegId());
        this.setLink(po.getLink());
        this.setContent(po.getContent());
        this.setImageUrl(po.getImageUrl());
        if (null != po.getState()) {
            this.state = State.getTypeByCode(po.getState().intValue());
        }
        this.setWeight(po.getWeight());
        this.setBeginDate(po.getBeginDate());
        this.setEndDate(po.getEndDate());
        this.setRepeats(po.getRepeats());
        this.setBeDefault(po.getBeDefault());
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
        this.setMessage(po.getMessage());
    }


    /**
     * 用vo构造po对象
     * @param vo vo对象
     */
    public AdvertiseBo(AdvertiseVo vo){
        this.setContent(vo.getContent());
        this.setBeginDate(vo.getBeginDate());
        this.setEndDate(vo.getEndDate());
        this.setWeight(vo.getWeight());
        if(vo.getRepeat()!=null){
            this.setRepeats(vo.getRepeat()?(byte)1:(byte)0);
        }
        this.setLink(vo.getLink());
    }

    /**
     * 构造po对象
     * @author xiaoru chen
     * @date 2020/11/25 17:32
     * @version 1.0
     */
    public AdvertisementPo creatPo() {
        AdvertisementPo po = new AdvertisementPo();
        po.setId(this.getId());
        po.setLink(this.getLink());
        po.setImageUrl(this.getImageUrl());
        po.setContent(this.getContent());
        po.setSegId(this.getSegId());
        if(this.getState()!=null) {
            po.setState(this.getState().getCode().byteValue());
        }
        po.setWeight(this.getWeight());
        po.setBeDefault(this.getBeDefault());
        po.setBeginDate(this.getBeginDate());
        po.setEndDate(this.getEndDate());
        po.setRepeats(this.getRepeats());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        po.setMessage(this.getMessage());
        return po;
    }

    /**
     * 构造vo对象
     * @author xiaoru chen
     * @date 2020/11/25 17:33
     * @version 1.0
     */
    @Override
    public Object createVo() {
        AdvertiseRetVo vo = new AdvertiseRetVo(this);
        return vo;
    }

    /**
     * 构造简单vo对象
     * @author xiaoru chen
     * @date 2020/11/25 17:34
     * @version 1.0
     */
    @Override
    public Object createSimpleVo() {
        AdvertiseRetVo vo = new AdvertiseRetVo(this);
        return vo;
    }

}
