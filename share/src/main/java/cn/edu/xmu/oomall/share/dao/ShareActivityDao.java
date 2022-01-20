package cn.edu.xmu.oomall.share.dao;

import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.mapper.ShareActivityPoMapper;
import cn.edu.xmu.oomall.share.model.bo.Share;
import cn.edu.xmu.oomall.share.model.bo.ShareActivity;
import cn.edu.xmu.oomall.share.model.bo.ShareActivityStrategy;
import cn.edu.xmu.oomall.share.model.po.BeSharePo;
import cn.edu.xmu.oomall.share.model.po.ShareActivityPo;
import cn.edu.xmu.oomall.share.model.po.ShareActivityPoExample;
import cn.edu.xmu.oomall.share.model.vo.ShareActivityRetVo;
import cn.edu.xmu.oomall.share.model.vo.ShareActivityVo;
import cn.edu.xmu.oomall.share.util.ShareCommon;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.lettuce.core.StrAlgoArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/11/25 21:13
 */
@Slf4j
@Repository
public class ShareActivityDao {

    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 根据spuId shopId 查询分享活动
     *
     * @param shareActivity
     * @return
     * @author Qiuyan Qian
     * @date Created in 2020/11/25 下午9:15
     */
    public ReturnObject<PageInfo<VoObject>> getShareActivities(ShareActivity shareActivity, Integer page, Integer pageSize) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();

        List<ShareActivityPo> shareActivityPos = null;

        //根据商品spuId查询
        if (null != shareActivity.getGoodsSkuId()) {
            log.debug("getShareActivities : goodsSkuId = " + shareActivity.getGoodsSkuId());
            criteria.andGoodsSkuIdEqualTo(shareActivity.getGoodsSkuId());
        }

        //根据shopId查询
        if (null != shareActivity.getShopId()) {
            log.debug("getShareActivities : shopId = " + shareActivity.getShopId());
            criteria.andShopIdEqualTo(shareActivity.getShopId());
        }

        //只查找未逻辑删除的 这里假定逻辑删除时 be_deleted字段为1 未删除为0
        //criteria.andStateEqualTo((byte) 0);
        PageHelper.startPage(page, pageSize);
        try {
            shareActivityPos = shareActivityPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,"数据库错误");
        }
        List<VoObject> ret = new ArrayList<>(shareActivityPos.size());
        for (ShareActivityPo po : shareActivityPos) {
            ShareActivity bo = new ShareActivity(po);
            ret.add(bo);
        }

        PageInfo<ShareActivityPo> shareActivityPoPageInfo = PageInfo.of(shareActivityPos);
        PageInfo<VoObject> shareActivityPage = new PageInfo<>(ret);
        shareActivityPage.setPages(shareActivityPoPageInfo.getPages());
        shareActivityPage.setPageNum(shareActivityPoPageInfo.getPageNum());
        shareActivityPage.setPageSize(shareActivityPoPageInfo.getPageSize());
        shareActivityPage.setTotal(shareActivityPoPageInfo.getTotal());

        return new ReturnObject<>(shareActivityPage);
    }


    /**
     * 根据商品id查询所有分享活动，包括已下架的
     * @param spuId 商品id
     * @return ReturnObject<ShareActivity>
     * @author Fiber W.
     * created at 12/3/20 10:31 AM
     */
    public ReturnObject<List<ShareActivity>> getShareActivityBySpuId(Long spuId) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();

        criteria.andGoodsSkuIdEqualTo(spuId);

        try {
            List<ShareActivityPo> shareActivityPoList = shareActivityPoMapper.selectByExample(example);
            List<ShareActivity> shareActivityList = shareActivityPoList.stream().map(ShareActivity::new).collect(Collectors.toList());

            return new ReturnObject<>(shareActivityList);
        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,"数据库错误");
        }
    }
    /**
     * 逻辑删除指定分享活动 店铺只能删除店内活动 管理员可以删除全部活动
     * @param shopId 店铺id(管理员为0)
     * @param spuId 商品spuId
     * @param id 分享活动id
     * @return
     * @author Qiuyan Qian
     * @date Created in 2020/11/25 下午10:27
     */
//    public ReturnObject deleteShareActivity(Long shopId, Long spuId, Long id){
//        ShareActivityPo shareActivityPo = null;
//        //通过分享活动id查找
//        shareActivityPo = shareActivityPoMapper.selectByPrimaryKey(id);
//        //校验活动id是否存在
//        if(shareActivityPo == null){
//            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该分享活动不存在"));
//        }
//        //校验活动是否被删除
//        if(shareActivityPo.getBeDeleted() != (byte)0){
//            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("该分享活动已经被删除"));
//        }
//        //如果不为管理员
//        if(shopId != 0L){
//            //如果找到的分享活动不是本店铺的活动
//            if(shareActivityPo.getShopId() != shopId){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("不是本店铺分享活动, 无法删除"));
//            }
//        }
//        //校验spuId和分享活动中的spuId是否一致
//        if(spuId != shareActivityPo.getGoodsSpuId()){
//            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("不是本分享活动的商品, 无法删除"));
//        }
//
//        //删除分享活动 即将is_deleted字段设为1 更新数据库
//        int ret;
//        ReturnObject<Object> retObj;
//        shareActivityPo.setBeDeleted((byte)1);
//        try{
//            ret = shareActivityPoMapper.updateByPrimaryKeySelective(shareActivityPo);
//        }catch (DataAccessException e){
//            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误"));
//        }
//
//        //检测是否更新成功
//        if(ret == 0){
//            log.debug("deleteShareActivity : shareActivityId = "+id+ "failed");
//            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//        }else{
//            log.debug("deleteShareActivity : shareActivityId = "+id+ "success");
//
//            //删除redis中的缓存
//            String redisKey = ShareCommon.getRedisKey(ShareActivity.class, id);
//            redisTemplate.delete(redisKey);
//            log.debug("deleteShareActivity : delete shareActivity "+id+"from redis");
//            retObj = new ReturnObject<>(ResponseCode.OK);
//        }
//
//        return retObj;
//    }

    /**
     * 修改分享活动状态
     *
     * @param shopId 店铺id(管理员为0)
     * @param id     分享活动id
     * @param state  要修改的状态 0为上线（此时be_deleted 为1 修改为0） 1为下线
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author Qiuyan Qian
     * @date Created in 2020/12/1 下午9:06
     */
    public ReturnObject updateShareActivityStatus(Long shopId, Long id, Byte state) {
        ShareActivityPo shareActivityPo = null;
        //通过分享活动id查找
        shareActivityPo = shareActivityPoMapper.selectByPrimaryKey(id);
        //校验活动id是否存在
        if (shareActivityPo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "该分享活动不存在");
        }
        //如果不为管理员
        if (!shopId.equals(0L)) {
            //如果找到的分享活动不是本店铺的活动
            if (!shareActivityPo.getShopId().equals(shopId)) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, "不是本店铺分享活动, 无法删除");
            }
        }

        shareActivityPo.setState(state);
        shareActivityPo.setGmtModified(LocalDateTime.now());

        int ret;
        ReturnObject<Object> retObj;
        try {
            ret = shareActivityPoMapper.updateByPrimaryKeySelective(shareActivityPo);
            return new ReturnObject<>(ResponseCode.OK);
        } catch (DataAccessException e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, "数据库错误");
        }

//        //检测是否更新成功
//        if (ret == 0) {
//            log.debug("updateShareActivityStatus : shareActivityId = " + id + "failed");
//            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//        } else {
//            log.debug("updateShareActivityStatus : shareActivityId = " + id + "success");
//
//            retObj =
//        }
//        return retObj;
    }

    /**
     * 新增分享活动
     * @param shareActivity
     * @return ReturnObject
     * @author Fiber W.
     * created at 12/3/20 11:18 AM
     */
    public ReturnObject<ShareActivity> insertShareActivity(ShareActivity shareActivity) {
        try {
            ShareActivityPo shareActivityPo = shareActivity.createPo();
            shareActivityPoMapper.insert(shareActivityPo);
            return new ReturnObject<>(new ShareActivity(shareActivityPo));
        } catch (Exception e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * 修改分享活动
     * @param shareActivity 分享活动bo
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author Fiber W.
     * created at 12/4/20 11:18 AM
     */
    public ReturnObject updateShareActivity(ShareActivity shareActivity) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(shareActivity.getId());
        criteria.andShopIdEqualTo(shareActivity.getShopId());
        criteria.andStateEqualTo(Byte.valueOf((byte) 0));

        try {
            int result = shareActivityPoMapper.updateByExampleSelective(shareActivity.createPo(), example);
            if (result != 0) {
                return new ReturnObject(ResponseCode.OK);
            }
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        } catch (Exception e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * 根据id查询分享活动
     * @param id 分享活动id
     * @return cn.edu.xmu.oomall.util.ReturnObject<cn.edu.xmu.oomall.share.model.bo.ShareActivity>
     * @author Fiber W.
     * created at 12/4/20 11:35 AM
     */
    public ReturnObject<ShareActivity> getShareActivityById(Long id) {
        try {
            ShareActivityPo po = shareActivityPoMapper.selectByPrimaryKey(id);
            if (po == null) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>(new ShareActivity(po));
        } catch (Exception e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 查看某商品当前上线的分享活动
     * @param skuId 商品id
     * @return cn.edu.xmu.oomall.util.ReturnObject<cn.edu.xmu.oomall.share.model.bo.ShareActivity>
     * @author Fiber W.
     * created at 12/4/20 2:52 PM
     */
    public ReturnObject<ShareActivity> getNowShareActivityBySpu(Long skuId) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsSkuIdEqualTo(skuId);
        criteria.andStateEqualTo(Byte.valueOf((byte) 1));
        criteria.andBeginTimeLessThanOrEqualTo(LocalDateTime.now());
        criteria.andEndTimeGreaterThanOrEqualTo(LocalDateTime.now());

        try {
            List<ShareActivityPo> shareActivityPoList = shareActivityPoMapper.selectByExample(example);
            if (shareActivityPoList.isEmpty()) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            ShareActivityPo po = shareActivityPoList.get(0);
            return new ReturnObject<>(new ShareActivity(po));
        } catch (Exception e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
    }
}
