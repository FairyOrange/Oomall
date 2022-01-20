package cn.edu.xmu.oomall.share.service;

import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.dao.BeShareDao;
import cn.edu.xmu.oomall.share.model.bo.BeShare;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Qiuyan Qian
 * @date Created in 2020/11/25 20:06
 */
@Service
public class BeShareService {
    @Autowired
    private BeShareDao beShareDao;

    @DubboReference(check = false)
    private IGoodsService iGoodsService;

    /**
     * 用户获得分享成功记录
     * @param beShare 分享成功记录bo
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param page
     * @param pageSize
     * @auther  Qiuyan Qian
     * @date  Created in 2020/11/25 下午8:08
    */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> getUserBeShares(BeShare beShare, String beginTime, String endTime, Integer page, Integer pageSize){
        return beShareDao.getUserBeShares(beShare, beginTime, endTime, page, pageSize);
    }

    /**
     * 添加分享成功记录
     * @param userId
     * @param skuId
     * @param shareId
     * cn.edu.xmu.oomall.util.ReturnObject<cn.edu.xmu.oomall.model.VoObject>
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 下午12:38
    */
    @Transactional
    public ReturnObject<VoObject> postBeShare(Long userId, Long skuId, Long shareId){
        return beShareDao.addBeShare(userId, skuId, shareId);
    }

    /**
     * 管理员查询所有分享成功记录
     * @param shopId
     * @param spuId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 下午6:31
    */
    @Transactional
    public ReturnObject getAdminBeShares(Long shopId, Long spuId, String beginTime, String endTime, Integer page, Integer pageSize){
        if (null == shopId) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("shopId为空"));
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
        return beShareDao.getAdminBeShares(spuId, beginTime, endTime, page, pageSize);
    }
}

