package cn.edu.xmu.oomall.share.dao;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.mapper.BeSharePoMapper;
import cn.edu.xmu.oomall.share.mapper.ShareActivityPoMapper;
import cn.edu.xmu.oomall.share.mapper.SharePoMapper;
import cn.edu.xmu.oomall.share.model.bo.*;
import cn.edu.xmu.oomall.share.model.po.*;
import cn.edu.xmu.oomall.share.util.ShareCommon;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Fiber W.
 * created at 11/17/20 3:51 PM
 * @detail cn.edu.xmu.oomall.share.dao.ShareDao
 */
@Slf4j
@Repository
public class ShareDao {

    @Autowired
    private BeSharePoMapper beSharePoMapper;

    @Autowired
    private SharePoMapper sharePoMapper;

    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private RedisTemplate<String, Integer> integerRedisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference(check = false)
    private IGoodsService iGoodsService;

    /**
     * ??????id????????????????????????
     * @param id ????????????id
     * @return cn.edu.xmu.oomall.share.model.bo.ShareActivityStrategy
     * @author Fiber W.
     * created at 11/23/20 1:28 PM
     */
    public ReturnObject<ShareActivityStrategy> getStrategyByShareActivityId(Long id) {
        String redisKey = ShareCommon.getRedisKey(ShareActivityStrategy.class, id);
        if (stringRedisTemplate.hasKey(redisKey)) {
            String json = stringRedisTemplate.opsForValue().get(redisKey);
            return new ReturnObject<>(new ShareActivityStrategy(json));
        }

        ShareActivityPo shareActivityPo = shareActivityPoMapper.selectByPrimaryKey(id);

        if (shareActivityPo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        String strategy = shareActivityPo.getStrategy();
        stringRedisTemplate.opsForValue().set(redisKey, strategy);

        return new ReturnObject<>(new ShareActivityStrategy(strategy));
    }

    /**
     * ??????userid???spuid????????????????????????
     * @param userId ?????????id
     * @param spuId ??????id
     * @param orderTime ????????????
     * @return cn.edu.xmu.oomall.util.ReturnObject<java.util.List<cn.edu.xmu.oomall.share.model.bo.BeShare>>
     * @author Fiber W.
     * created at 11/24/20 11:04 AM
     */
    public ReturnObject<List<BeShare>> getBeShareByUserIdAndSpuId(Long userId, Long spuId, LocalDateTime orderTime) {
        BeSharePoExample beSharePoExample = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = beSharePoExample.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        criteria.andGoodsSkuIdEqualTo(spuId);
        criteria.andOrderIdIsNull();
        criteria.andGmtCreateLessThanOrEqualTo(orderTime);

        try {
            List<BeSharePo> beSharePoList = beSharePoMapper.selectByExample(beSharePoExample);
            List<BeShare> beShareList = beSharePoList.stream().map(BeShare::new).collect(Collectors.toList());
            Collections.sort(beShareList);

            return new ReturnObject<>(beShareList);
        } catch (DataAccessException e) {
            log.error("insert DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("??????????????????%s", e.getMessage()));
        } catch (Exception e) {
            log.debug("ShareDao:getBeShareByUserIdAndSpuId?????????????????????" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("???????????????%s", e.getMessage()));
        }


    }

    /**
     * ??????shareid??????????????????????????????
     * @param shareId ??????id
     * @return cn.edu.xmu.oomall.util.ReturnObject<java.lang.Long>
     * @author Fiber W.
     * created at 11/24/20 12:37 PM
     */
    public ReturnObject<Integer> getQuantityById(Long shareId) {
        String redisKey = ShareCommon.getRedisKey(Share.class, shareId);
        if (integerRedisTemplate.hasKey(redisKey)) {
            return new ReturnObject<>(integerRedisTemplate.opsForValue().get(redisKey));
        }
        SharePo sharePo = sharePoMapper.selectByPrimaryKey(shareId);
        if (sharePo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        integerRedisTemplate.opsForValue().setIfAbsent(redisKey, sharePo.getQuantity());

        return new ReturnObject<>(sharePo.getQuantity());
    }

    /**
     * ??????????????????
     * @param shareId ??????id
     * @param increase ???????????????????????????0.01??????
     * @return cn.edu.xmu.oomall.util.ReturnObject<java.lang.Integer>
     * @author Fiber W.
     * created at 11/25/20 3:52 PM
     */
    public ReturnObject<Long> increaseQuantity(Long shareId, Integer increase) {
        String redisKey = ShareCommon.getRedisKey(Share.class, shareId);
        if (! integerRedisTemplate.hasKey(redisKey)) {
            getQuantityById(shareId);
        }
        Long newValue = integerRedisTemplate.opsForValue().increment(redisKey, increase);
        return new ReturnObject<>(newValue);
    }

    /**
     * ????????????id????????????
     * @param shareId ??????id
     * @return ReturnObject
     * @author Fiber W.
     * created at 11/29/20 3:20 PM
     */
    public ReturnObject setShareQuantity(Long shareId) {
        SharePo sharePo = new SharePo();
        sharePo.setId(shareId);
        String redisKey = ShareCommon.getRedisKey(Share.class, shareId);

        Integer quantity = integerRedisTemplate.opsForValue().get(redisKey);
        sharePo.setQuantity(quantity);

        try {
            sharePoMapper.updateByPrimaryKeySelective(sharePo);
        } catch (DataAccessException e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("???????????????"));
        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("????????????"));
        }
        return new ReturnObject(ResponseCode.OK);
    }

    /**
     * ???????????????????????? ?????????spuId beginTime endTime ??????
     * @param share     bo??????
     * @param beginTime ????????????
     * @param endTime   ????????????
     * @param page
     * @param pageSize
     * @return <PageInfo<VoObject>>
     * @date Created in 2020/11/24 22:15
     */
    public ReturnObject<PageInfo<VoObject>> getUserShares(Share share, String beginTime, String endTime, Integer page, Integer pageSize){
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();

        List<SharePo> sharePos = null;

        //????????????id?????? ????????????
        if (null != share.getSharerId()){
            log.debug("sharer id = " + share.getSharerId());
            criteria.andSharerIdEqualTo(share.getSharerId());
        } else {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "?????????id??????");
        }

        //????????????id??????
        if (null != share.getGoodsSkuId()) {
            criteria.andGoodsSkuIdEqualTo(share.getGoodsSkuId());
        }

        //??????beginTime???endTime?????????????????????????????????
        if(!ShareCommon.judgeTimeValid(beginTime)||!ShareCommon.judgeTimeValid(endTime)){
            List<VoObject> ret = new ArrayList<>();
            PageInfo<VoObject> beSharePage = new PageInfo<>(ret);
            beSharePage.setPages(0);
            beSharePage.setPageNum(page);
            beSharePage.setPageSize(pageSize);
            beSharePage.setTotal(0);

            return new ReturnObject<>(beSharePage);
        }
        //??????????????????
        LocalDateTime beginTimeLDT = ShareCommon.changeStringToLocalDateTime(beginTime);
        LocalDateTime endTimeLDT = ShareCommon.changeStringToLocalDateTime(endTime);

//        //???????????????????????????????????? ????????????????????????????????????
//        if (null != beginTimeLDT && null != endTimeLDT && beginTimeLDT.isAfter(endTimeLDT)) {
//            log.error("getUserShares: beginTime: " + beginTime + " is after endTime: " + endTime);
//            return new ReturnObject<>(ResponseCode.Log_Bigger, "?????????????????????????????????");
//        }
        if (null != beginTime) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTimeLDT);
        }
        if (null != endTime) {
            criteria.andGmtCreateLessThanOrEqualTo(endTimeLDT);
        }
        PageHelper.startPage(page,pageSize);
        try {
            sharePos = sharePoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, "???????????????");
        }
        List<VoObject> ret = new ArrayList<>(sharePos.size());
        for (SharePo po : sharePos) {
            Share bo = new Share(po);
            try
            {
                ReturnObject<SkuInfoDTO> skuRet = iGoodsService.getSelectSkuInfoBySkuId(po.getGoodsSkuId());
                if (skuRet.getCode() == ResponseCode.OK) {
                    bo.setSku(new SkuInfo(skuRet.getData()));
                    ret.add(bo);
                }
            } catch (Exception e) {
                log.error("????????????dubbo???????????????:iGoodsService.getSelectSkuInfoBySkuId");
                ret.add(bo);
            }
        }

        PageInfo<SharePo> sharePoPageInfo = PageInfo.of(sharePos);
        PageInfo<VoObject> sharePage = new PageInfo<>(ret);
        sharePage.setPages(sharePoPageInfo.getPages());
        sharePage.setPageNum(sharePoPageInfo.getPageNum());
        sharePage.setPageSize(sharePoPageInfo.getPageSize());
        sharePage.setTotal(sharePoPageInfo.getTotal());

        return new ReturnObject<>(sharePage);
    }

    /**
     * ?????????????????? ?????????spuIds(List)????????????spuId??????
     * @param skuId
     * @param page
     * @param pageSize
     * cn.edu.xmu.oomall.util.ReturnObject<com.github.pagehelper.PageInfo<cn.edu.xmu.oomall.model.VoObject>>
     * @author  Qiuyan Qian
     * @date  Created in 2020/12/1 ??????9:01
    */
    public ReturnObject<PageInfo<VoObject>> getAllShares(Long skuId, Integer page, Integer pageSize){
        SharePoExample example = new SharePoExample();
        SharePoExample.Criteria criteria = example.createCriteria();

        List<SharePo> sharePos = null;

        criteria.andGoodsSkuIdEqualTo(skuId);

        PageHelper.startPage(page,pageSize);
        try{
            sharePos = sharePoMapper.selectByExample(example);
        }catch (DataAccessException e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, "???????????????");
        }
        List<VoObject> ret = new ArrayList<>(sharePos.size());
        for (SharePo po : sharePos) {
            Share bo = new Share(po);
            try
            {
                ReturnObject<SkuInfoDTO> skuRet = iGoodsService.getSelectSkuInfoBySkuId(po.getGoodsSkuId());
                if (skuRet.getCode() == ResponseCode.OK) {
                    bo.setSku(new SkuInfo(skuRet.getData()));
                    ret.add(bo);
                }
            } catch (Exception e) {
                log.error("????????????dubbo???????????????:iGoodsService.getSelectSkuInfoBySkuId");
                ret.add(bo);
            }
        }

        PageInfo<SharePo> sharePoPageInfo = PageInfo.of(sharePos);
        PageInfo<VoObject> sharePage = new PageInfo<>(ret);
        sharePage.setPages(sharePoPageInfo.getPages());
        sharePage.setPageNum(sharePoPageInfo.getPageNum());
        sharePage.setPageSize(sharePoPageInfo.getPageSize());
        sharePage.setTotal(sharePoPageInfo.getTotal());

        return new ReturnObject<>(sharePage);
    }

    /**
     * ????????????
     * @param share ??????
     * @return cn.edu.xmu.oomall.util.ReturnObject
     * @author Fiber W.
     * created at 12/4/20 2:44 PM
     */
    public ReturnObject<VoObject> addNewShare(Share share) {
        share.setGmtCreate(LocalDateTime.now());
        share.setQuantity(Integer.valueOf(0));
        SharePo po = share.createPo();

        try {

            int result = sharePoMapper.insert(po);
            Share retShare = new Share(po);
            try
            {
                ReturnObject<SkuInfoDTO> skuRet = iGoodsService.getSelectSkuInfoBySkuId(po.getGoodsSkuId());
                if (skuRet.getCode() != ResponseCode.OK) {
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                }
                retShare.setSku(new SkuInfo(skuRet.getData()));
                return new ReturnObject<>(retShare);
            } catch (Exception e) {
                return new ReturnObject<>(retShare);
            }

        } catch (DataAccessException e) {
            SharePoExample example = new SharePoExample();
            SharePoExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsSkuIdEqualTo(share.getGoodsSkuId());
            criteria.andSharerIdEqualTo(share.getSharerId());
            criteria.andShareActivityIdEqualTo(share.getShareActivityId());
            try {
                Share share1 = new Share(sharePoMapper.selectByExample(example).get(0));
                try {
                    ReturnObject<SkuInfoDTO> skuRet = iGoodsService.getSelectSkuInfoBySkuId(share1.getGoodsSkuId());
                    share1.setSku(new SkuInfo(skuRet.getData()));
                    return new ReturnObject(share1);
                } catch (Exception exception) {
                    return new ReturnObject(share1);
                }
            } catch (Exception e1) {
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, "???????????????");
            }

        } catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }
}
