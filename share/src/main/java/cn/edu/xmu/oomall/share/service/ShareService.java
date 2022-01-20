package cn.edu.xmu.oomall.share.service;


import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.dao.BeShareDao;
import cn.edu.xmu.oomall.share.dao.ShareActivityDao;
import cn.edu.xmu.oomall.share.dao.ShareDao;
import cn.edu.xmu.oomall.share.model.bo.BeShare;
import cn.edu.xmu.oomall.share.model.bo.ShareActivity;
import cn.edu.xmu.oomall.share.model.bo.ShareActivityStrategy;
import cn.edu.xmu.oomall.util.*;

import cn.edu.xmu.oomall.share.model.bo.Share;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description ShareService
 * @author Fiber W.
 * created at 11/17/20 3:40 PM
 */
@Slf4j
@Service
public class ShareService {

    @Autowired
    private ShareDao shareDao;

    @Autowired
    private BeShareDao beShareDao;

    @Autowired
    private ShareActivityDao shareActivityDao;

    @Autowired
    private ShareActivityTypeFactory shareActivityTypeFactory;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @DubboReference
    private IGoodsService iGoodsService;


    /**
     * 计算分享返点
     * @param orderId 订单详情id
     * @param userId 用户id
     * @param quantity 购买数量
     * @param price 商品单价
     * @param spuId 商品spuId
     * @param gmtCreated 订单创建时间
     * @return cn.edu.xmu.oomall.util.ReturnObject<cn.edu.xmu.oomall.model.VoObject> 是否计算成功，以及BeshareID
     * @author Fiber W.
     * created at 11/17/20 3:55 PM
     */
    @Transactional
    public ReturnObject<List<Long>> setShareRebate(Long orderId, Long userId, Integer quantity, Long price, Long spuId, LocalDateTime gmtCreated) {
        String redisKey = "bs_lock" + userId + "_" + spuId;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, 0);
        if (! success) {
            return new ReturnObject<>(ResponseCode.ORDERITEM_NOTSHARED);
        }
        ReturnObject<List<BeShare>> returnObject = shareDao.getBeShareByUserIdAndSpuId(userId, spuId, gmtCreated);
        if (returnObject.getCode() != ResponseCode.OK) {
            return new ReturnObject<>(returnObject.getCode());
        }
        List<BeShare> beShareList = returnObject.getData();

        if (beShareList.isEmpty()) {
            return new ReturnObject<>(ResponseCode.ORDERITEM_NOTSHARED);
        }
//        //按照分享活动id分组
//        Map<Long, List<BeShare>> beShareListMap = groupByShareActivityId(beShareList);
//        //对分享id降序排列
//        List<Long> keyList = beShareListMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
//
//        if (keyList.isEmpty()) {
//            return new ReturnObject<>(ResponseCode.ORDERITEM_NOTSHARED);
//        }
        //获取该分享活动下的分享记录
        List<BeShare> successShareList = beShareList.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        //获取分享规则
        ShareActivityStrategy shareActivityStrategy = shareDao.getStrategyByShareActivityId(successShareList.get(0).getShareActivityId()).getData();

        List<BeShare> aftercalcBeShare = shareActivityStrategy.calcRebate(shareDao, price, quantity * 100, successShareList, shareActivityTypeFactory, orderId, rocketMQTemplate);
        sendRebateToMQ(aftercalcBeShare);

        List<Long> beShareIdList = new ArrayList<>();

        for (BeShare b :
                aftercalcBeShare) {
            if (b.getOrderId() != Long.valueOf(0) && b.getOrderId() != null) {
                beShareIdList.add(b.getId());
            }
        }

        return new ReturnObject<>(beShareIdList);
    }
    
    /**
     * 将成功返点的beshare通过rocketmq写入数据库
     * @param beShareList 成功返点的beshare列表
     * @return void
     * @author Fiber W.
     * created at 11/28/20 7:04 PM
     */
    private void sendRebateToMQ(List<BeShare> beShareList) {
        String s = JacksonUtil.toJson(beShareList);
        log.debug("发送返点");
        rocketMQTemplate.asyncSend("rebate-topic", MessageBuilder.withPayload(s).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("sendOrderPayMessage: onSuccess result = "+ sendResult+" time ="+LocalDateTime.now());
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("sendOrderPayMessage: onException e = "+ throwable.getMessage()+" time ="+LocalDateTime.now());
            }
        });
        //beShareDao.setBeShareRebate(beShareList);
    }

    /**
     * 按分享活动id将分享对象分组
     * @param beShareList 分享成功列表
     * @return Map<List<cn.edu.xmu.oomall.share.model.bo.BeShare>>
     * @author Fiber W.
     * created at 11/24/20 3:49 PM
     */
    private Map<Long, List<BeShare>> groupByShareActivityId(List<BeShare> beShareList) {
        Map<Long, List<BeShare>> lists = new HashMap<>();
        for (BeShare beShare :
                beShareList) {
            if (beShare.getShareActivityId() == null) {
                continue;
            }
            if (lists.containsKey(beShare.getShareActivityId())) {
                lists.get(beShare.getShareActivityId()).add(beShare);
            } else {
                List<BeShare> beShareList1 = new ArrayList<>();
                beShareList1.add(beShare);
                lists.put(beShare.getShareActivityId(), beShareList1);
            }
        }

        return lists;
    }


    /**
     * 用户查询所有分享记录
     * @param share     bo对象
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param page      页码
     * @param pageSize  页数
     * @return ReturnObject<PageInfo<VoObject>>
     * @author Qiuyan Qian
     * Created in 2020/11/25 22:05
     */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> getUserShares(Share share, String beginTime, String endTime, Integer page, Integer pageSize){
        return shareDao.getUserShares(share,beginTime,endTime,page,pageSize);
    }


    /**
     * 管理员查询商品分享记录 店铺管理员只能查询自己店铺内的
     * @param shopId
     * @param spuId
     * @param page
     * @param pageSize
     * @return cn.edu.xmu.oomall.util.ReturnObject<com.github.pagehelper.PageInfo<cn.edu.xmu.oomall.model.VoObject>>
     * @author  Qiuyan Qian
     * Created in 2020/12/1 上午9:13
    */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> getAdminShares(Long shopId, Long spuId, Integer page, Integer pageSize){
        if(null == shopId){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "shopId为空");
        }
        //如果是管理员 可以查找所有的商品
        if (! shopId.equals(0L)) {
            try {
                ReturnObject<List<Long>> returnObject = iGoodsService.getAllSkuIdByShopId(shopId);

                //没有查询到店铺内的spuId
                if(returnObject.getCode() != ResponseCode.OK) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "该店铺 id ="+shopId+" 不存在商品");
                }
                List<Long> shopSpuIds = returnObject.getData();
                if(! shopSpuIds.contains(spuId)){
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "该店铺id = "+shopId+" 不存在该商品 spuId = "+spuId);
                }
            } catch (Exception e) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return shareDao.getAllShares(spuId, page, pageSize);
    }

    /**
     * 分享者生成分享链接
     * @param share 分享
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author Fiber W.
     * created at 12/4/20 3:02 PM
     */
    public ReturnObject<VoObject> addShare(Share share) {
        ReturnObject<ShareActivity> ret = shareActivityDao.getNowShareActivityBySpu(share.getGoodsSkuId());
        if (ret.getCode() != ResponseCode.OK) {
            return new ReturnObject<>(ret.getCode());
        }
        ShareActivity shareActivity = ret.getData();
        share.setShareActivityId(shareActivity.getId());

        return shareDao.addNewShare(share);
    }

    /**
     * 判断sku当前是否可分享
     * @param skuId 商品skuId
     * @return cn.edu.xmu.oomall.util.ReturnObject<java.lang.Boolean>
     * @author Fiber W.
     * created at 12/15/20 10:57 AM
     */
    public ReturnObject<Boolean> skuShareable(Long skuId) {
        ReturnObject<ShareActivity> ret = shareActivityDao.getNowShareActivityBySpu(skuId);
        if (ret.getCode() != ResponseCode.OK) {
            return new ReturnObject<>(false);
        }
        return new ReturnObject<>(true);
    }
}
