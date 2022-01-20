package cn.edu.xmu.oomall.share.service;

import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.dao.ShareActivityDao;
import cn.edu.xmu.oomall.share.model.bo.ShareActivity;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Fiber W.
 * created at 11/20/20 3:16 PM
 * @detail cn.edu.xmu.oomall.share.service
 */
@Service
public class ShareActivityService {
    @Autowired
    private ShareActivityDao shareActivityDao;


    @DubboReference
    private IGoodsService iGoodsService;

    @Value("${share.share-activity.share-activity-strategy}")
    private String strategySchema;

    /**
     * 获得所有分享活动 通过spuId shopId查找
     * @param shareActivity 分享活动bo
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo<VoObject>>
     * @author  Qiuyan Qian
     * Created in 2020/11/25 下午11:50
    */
    @Transactional
    public ReturnObject<PageInfo<VoObject>> getShareActivities(ShareActivity shareActivity, Integer page, Integer pageSize){
        return shareActivityDao.getShareActivities(shareActivity, page, pageSize);
    }

    /**
     * 逻辑删除指定分享活动 商家可以删除自己店铺内的活动 管理员(shopId为0)可以删除所有
     * @param shopId  店铺id
     * @param id 分享活动id
     * @return ReturnObject
     * @author  Qiuyan Qian
     * Created in 2020/11/25 下午11:57
    */
    @Transactional
    public ReturnObject deleteShareActivity(Long shopId, Long id){
        return shareActivityDao.updateShareActivityStatus(shopId, id, (byte)0);
    }

    /**
     * 上架指定分享活动 商家只能上架自己店铺内的活动
     * @param shopId 店铺id
     * @param id 分享活动id
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 下午9:58
    */
    @Transactional
    public ReturnObject recoverShareActivity(Long shopId, Long id){
        return shareActivityDao.updateShareActivityStatus(shopId, id, (byte)1);
    }


    /**
     * 新建分享活动
     * @param shareActivity 分享活动
     * @return ReturnObject
     * @author Fiber W.
     * created at 12/2/20 11:29 AM
     */
    @Transactional
    public ReturnObject addShareActivity(ShareActivity shareActivity) {
        if (! checkStrategy(shareActivity.getStrategy())) {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if (! shareActivity.check()) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        if (! shareActivity.checkTime()) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        if (! shareActivity.getShopId().equals(Long.valueOf(0))) {
            try {
                ReturnObject<Long> returnObject = iGoodsService.getShopIdBySkuId(shareActivity.getGoodsSkuId());
                if (returnObject.getCode() != ResponseCode.OK) {
                    return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
                }
                Long shopIdTrue = returnObject.getData();
                if (! shopIdTrue.equals(shareActivity.getShopId())) {
                    return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
                }
            } catch (Exception e) {
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }

        }

        List<ShareActivity> shareActivityList = shareActivityDao.getShareActivityBySpuId(shareActivity.getGoodsSkuId()).getData();
        if (! checkShareActivityTime(shareActivity, shareActivityList)) {
            return new ReturnObject(ResponseCode.SHAREACT_CONFLICT);
        }
        return shareActivityDao.insertShareActivity(shareActivity);

    }

    /**
     * 修改分享活动
     * @param shareActivity 分享活动
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author Fiber W.
     * created at 12/4/20 11:06 AM
     */
    @Transactional
    public ReturnObject updateShareActivity(ShareActivity shareActivity) {
        if (! checkStrategy(shareActivity.getStrategy())) {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if (! shareActivity.check()) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        if (! shareActivity.checkTime()) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        ReturnObject<ShareActivity> existActivity = shareActivityDao.getShareActivityById(shareActivity.getId());
        if (existActivity.getCode() != ResponseCode.OK) {
            return existActivity;
        }
        List<ShareActivity> shareActivityList = shareActivityDao.getShareActivityBySpuId(existActivity.getData().getGoodsSkuId()).getData();
        if (! checkShareActivityTime(shareActivity, shareActivityList)) {
            return new ReturnObject(ResponseCode.SHAREACT_CONFLICT);
        }
        return shareActivityDao.updateShareActivity(shareActivity);
    }
    /**
     * 检验分享活动时间是否正确
     * @param shareActivity 分享活动
     * @return
     * @author Fiber W.
     * created at 12/3/20 10:46 AM
     */
    private boolean checkShareActivityTime(ShareActivity shareActivity, List<ShareActivity> shareActivityList) {
        for (ShareActivity activity : shareActivityList) {
            if (shareActivity.getId() != null) {
                if (shareActivity.getId().equals(activity.getId())) {
                    continue;
                }
            }
            if (shareActivity.getEndTime().isAfter(activity.getBeginTime()) && shareActivity.getBeginTime().isBefore(activity.getEndTime())) {
                return false;
            }
        }
        return true;

    }

    /**
     * 使用schema检验分享规则
     * @param strategy 规则
     * @return Boolean
     * @author Fiber W.
     * created at 12/4/20 10:56 AM
     */
    private boolean checkStrategy(String strategy) {
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        try {
            JsonNode schemaNode = JsonLoader.fromString(this.strategySchema);
            JsonNode dataNode = JsonLoader.fromString(strategy);
            JsonSchema schema = factory.getJsonSchema(schemaNode);
            ProcessingReport processingReport = schema.validate(dataNode);
            if (! processingReport.isSuccess()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
