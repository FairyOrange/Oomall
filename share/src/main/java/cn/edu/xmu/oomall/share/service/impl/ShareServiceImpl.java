package cn.edu.xmu.oomall.share.service.impl;

import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.other.service.IShareService;
import cn.edu.xmu.oomall.share.service.BeShareService;
import cn.edu.xmu.oomall.share.service.ShareService;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ResponseUtil;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Fiber W.
 * created at 12/6/20 8:32 PM
 * @detail cn.edu.xmu.oomall.share.service.impl
 */
@DubboService
public class ShareServiceImpl implements IShareService {

    @Autowired
    private ShareService shareService;

    @Autowired
    private BeShareService beShareService;

    /**
     * 设置分享返点
     * @param orderId 订单详细id
     * @param userId 用户id
     * @param quantity 数量
     * @param price 单价
     * @param skuId skuid
     * @param gmtCreated 订单创建时间
     * @return cn.edu.xmu.oomall.util.ReturnObject<java.util.List<java.lang.Long>>
     * @author Fiber W.
     * created at 12/6/20 8:34 PM
     */
    @Override
    public ReturnObject<List<Long>> setShareRebate(Long orderId, Long userId, Integer quantity, Long price, Long skuId, LocalDateTime gmtCreated) {
        return shareService.setShareRebate(orderId, userId, quantity, price, skuId, gmtCreated);
    }

    /**
     * 判断sid和skuid是否匹配
     * @param sid 分享id
     * @param skuId 商品id
     * @param userId 用户id
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author Fiber W.
     * created at 12/10/20 2:05 PM
     */
    @Override
    public ReturnObject<Boolean> shareUserSkuMatch(Long sid, Long skuId, Long userId) {
        ReturnObject<VoObject> returnObject = beShareService.postBeShare(userId, skuId, sid);
        if (returnObject.getCode() == ResponseCode.OK) {
            return new ReturnObject<>(Boolean.TRUE);
        }
        return new ReturnObject<>(Boolean.FALSE);
    }

    /**
     * 判断SKU是否可分享
     * @param skuId
     * @return
     */
    public ReturnObject<Boolean> skuSharable(Long skuId) {
        return shareService.skuShareable(skuId);
    }
}
