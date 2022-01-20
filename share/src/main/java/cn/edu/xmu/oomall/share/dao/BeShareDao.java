package cn.edu.xmu.oomall.share.dao;

import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.share.mapper.BeSharePoMapper;
import cn.edu.xmu.oomall.share.mapper.SharePoMapper;
import cn.edu.xmu.oomall.share.model.bo.BeShare;
import cn.edu.xmu.oomall.share.model.bo.Share;
import cn.edu.xmu.oomall.share.model.bo.SkuInfo;
import cn.edu.xmu.oomall.share.model.po.BeSharePo;
import cn.edu.xmu.oomall.share.model.po.BeSharePoExample;
import cn.edu.xmu.oomall.share.model.po.SharePo;
import cn.edu.xmu.oomall.share.model.po.SharePoExample;
import cn.edu.xmu.oomall.share.util.ShareCommon;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Qiuyan Qian
 * @date Created in 2020/11/25 19:21
 */
@Slf4j
@Repository
public class BeShareDao {

    @Autowired
    private BeSharePoMapper beSharePoMapper;

    @Autowired
    private SharePoMapper sharePoMapper;

    @DubboReference(check = false)
    private IGoodsService iGoodsService;

    /**
     * 用户获取分享成功记录 可以通过条件筛选
     *
     * @param beShare   分享成功记录bo
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param page
     * @param pageSize
     * @return ReturnObject<PageInfo < VoObject>>
     * @auther Qiuyan Qian
     * @date Created in 2020/11/25 下午7:50
     */
    public ReturnObject<PageInfo<VoObject>> getUserBeShares(BeShare beShare, String beginTime, String endTime, Integer page, Integer pageSize) {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();

        List<BeSharePo> beSharePos = null;

        //根据用户id查询 不得为空
        if (null != beShare.getSharerId()) {
            log.debug("sharer id = " + beShare.getSharerId());
            criteria.andSharerIdEqualTo(beShare.getSharerId());
        } else {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "分享者id为空");
        }

        //根据商品id查询
        if (null != beShare.getGoodsSkuId()) {
            criteria.andGoodsSkuIdEqualTo(beShare.getGoodsSkuId());
        }
        //判断beginTime和endTime是否有传入时间格式错误
        if(!ShareCommon.judgeTimeValid(beginTime)||!ShareCommon.judgeTimeValid(endTime)){
            List<VoObject> ret = new ArrayList<>();
            PageInfo<VoObject> beSharePage = new PageInfo<>(ret);
            beSharePage.setPages(0);
            beSharePage.setPageNum(page);
            beSharePage.setPageSize(pageSize);
            beSharePage.setTotal(0);

            return new ReturnObject<>(beSharePage);
        }
        //根据时间查询
        LocalDateTime beginTimeLDT = ShareCommon.changeStringToLocalDateTime(beginTime);
        LocalDateTime endTimeLDT = ShareCommon.changeStringToLocalDateTime(endTime);

        //起始时间和终止时间不为空 且起始时间在终止时间之后
//        if (null != beginTimeLDT && null != endTimeLDT && beginTimeLDT.isAfter(endTimeLDT)) {
//            log.error("getUserBeShares: beginTime: " + beginTime + " is after endTime: " + endTime);
//            return new ReturnObject<>(ResponseCode.Log_Bigger, "开始时间在结束时间之后");
//        }
        if (null != beginTime) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTimeLDT);
        }
        if (null != endTime) {
            criteria.andGmtCreateLessThanOrEqualTo(endTimeLDT);
        }

        PageHelper.startPage(page, pageSize);
        try {
            beSharePos = beSharePoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
        List<VoObject> ret = new ArrayList<>(beSharePos.size());
        for (BeSharePo po : beSharePos) {
            BeShare bo = new BeShare(po);
            try {
                ReturnObject<SkuInfoDTO> skuRet = iGoodsService.getSelectSkuInfoBySkuId(po.getGoodsSkuId());
                if (skuRet.getCode() == ResponseCode.OK) {
                    bo.setSku(new SkuInfo(skuRet.getData()));
                    ret.add(bo);
                }
            } catch (Exception e) {
                log.error("调用远程dubbo接口失败！:iGoodsService.getUserBeShares");
                ret.add(bo);
            }

        }


        PageInfo<BeSharePo> beSharePoPageInfo = PageInfo.of(beSharePos);
        PageInfo<VoObject> beSharePage = new PageInfo<>(ret);
        beSharePage.setPages(beSharePoPageInfo.getPages());
        beSharePage.setPageNum(beSharePoPageInfo.getPageNum());
        beSharePage.setPageSize(beSharePoPageInfo.getPageSize());
        beSharePage.setTotal(beSharePoPageInfo.getTotal());

        return new ReturnObject<>(beSharePage);
    }

    /**
     * 修改beshare表
     *
     * @param beShareList
     * @return
     * @author Fiber W.
     * created at 11/28/20 7:07 PM
     */
    public void setBeShareRebate(List<BeShare> beShareList) {
        try {
            for (BeShare beShare :
                    beShareList) {
                beSharePoMapper.updateByPrimaryKeySelective(beShare.createPo());
            }
        } catch (DataAccessException e) {
            log.error("数据库错误" + e.getMessage());
        } catch (Exception e) {
            log.error("严重错误" + e.getMessage());
        }
    }


    /**
     * 修改beshare表
     *
     * @param beShare
     * @return
     * @author Fiber W.
     * created at 11/28/20 7:07 PM
     */
    public void setBeShareRebate(BeShare beShare) {
        try {
            beSharePoMapper.updateByPrimaryKeySelective(beShare.createPo());
        } catch (DataAccessException e) {
            log.error("数据库错误" + e.getMessage());
        } catch (Exception e) {
            log.error("严重错误" + e.getMessage());
        }
    }

    /**
     * 向beShared表中增加一条记录 通过shareId查找到shareActivityId sharerId
     *
     * @param userId
     * @param skuId
     * @param shareId void
     * @author Qiuyan Qian
     * @date Created in 2020/12/1 上午11:50
     */
    public ReturnObject<VoObject> addBeShare(Long userId, Long skuId, Long shareId) {
        //查询share表
        SharePo sharePo = null;
        try {
            sharePo = sharePoMapper.selectByPrimaryKey(shareId);
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
        if (null == sharePo) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "分享记录不存在");
        }
        BeSharePo beSharePo = new BeSharePo();
        //路径中的skuId和查询share表所得不同
        if (!skuId.equals(sharePo.getGoodsSkuId())) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "skuId与shareId不匹配");
        }
        beSharePo.setCustomerId(userId);
        beSharePo.setShareId(shareId);
        beSharePo.setGoodsSkuId(skuId);
        beSharePo.setSharerId(sharePo.getSharerId());
        beSharePo.setRebate(0);
        beSharePo.setGmtCreate(LocalDateTime.now());
        beSharePo.setGmtModified(LocalDateTime.now());
        beSharePo.setShareActivityId(sharePo.getShareActivityId());
        try {
            int ret = beSharePoMapper.insertSelective(beSharePo);
            if (ret == 0) {
                //新建分享成功记录失败
                log.debug("新建分享成功记录失败 userId : " + beSharePo.getCustomerId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新建分享成功记录失败 userId : " + beSharePo.getCustomerId());
            } else {
                //新建分享成功记录成功
                //使用po构建bo 反射机制 获取id
                BeShare beShare = new BeShare(beSharePo);
                return new ReturnObject<>(beShare);
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }

    /**
     * 通过spuIds筛选所有分享成功记录
     *
     * @param spuId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return cn.edu.xmu.oomall.util.ReturnObject<com.github.pagehelper.PageInfo < cn.edu.xmu.oomall.model.VoObject>>
     * @author Qiuyan Qian
     * @date Created in 2020/12/1 下午3:52
     */
    public ReturnObject getAdminBeShares(Long spuId, String beginTime, String endTime, Integer page, Integer pageSize) {
        BeSharePoExample example = new BeSharePoExample();
        BeSharePoExample.Criteria criteria = example.createCriteria();

        List<BeSharePo> beSharePos = null;

        criteria.andGoodsSkuIdEqualTo(spuId);

        //判断beginTime和endTime是否有传入时间格式错误
        if(!ShareCommon.judgeTimeValid(beginTime)||!ShareCommon.judgeTimeValid(endTime)){
            List<VoObject> ret = new ArrayList<>();
            PageInfo<VoObject> beSharePage = new PageInfo<>(ret);
            beSharePage.setPages(0);
            beSharePage.setPageNum(page);
            beSharePage.setPageSize(pageSize);
            beSharePage.setTotal(0);

            return new ReturnObject<>(beSharePage);
        }

        //根据时间查询
        LocalDateTime beginTimeLDT = ShareCommon.changeStringToLocalDateTime(beginTime);
        LocalDateTime endTimeLDT = ShareCommon.changeStringToLocalDateTime(endTime);

        //起始时间和终止时间不为空 且起始时间在终止时间之后
//        if (null != beginTimeLDT && null != endTimeLDT && beginTimeLDT.isAfter(endTimeLDT)) {
//            log.error("getAdminBeShares: beginTime: " + beginTime + " is after endTime: " + endTime);
//            return new ReturnObject<>(ResponseCode.Log_Bigger, "开始时间在结束时间之后");
//        }
        if (null != beginTime) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTimeLDT);
        }
        if (null != endTime) {
            criteria.andGmtCreateLessThanOrEqualTo(endTimeLDT);
        }

        PageHelper.startPage(page, pageSize);
        try {
            beSharePos = beSharePoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
        List<VoObject> ret = new ArrayList<>(beSharePos.size());
        for (BeSharePo po : beSharePos) {
            BeShare bo = new BeShare(po);
            try
            {
                ReturnObject<SkuInfoDTO> skuRet = iGoodsService.getSelectSkuInfoBySkuId(po.getGoodsSkuId());
                if (skuRet.getCode() == ResponseCode.OK) {
                    bo.setSku(new SkuInfo(skuRet.getData()));
                    ret.add(bo);
                }
            } catch (Exception e) {
                log.error("调用远程dubbo接口失败！:iGoodsService.getAdminBeShares");
                ret.add(bo);
            }


        }

        PageInfo<BeSharePo> beSharePoPageInfo = PageInfo.of(beSharePos);
        PageInfo<VoObject> beSharePage = new PageInfo<>(ret);
        beSharePage.setPages(beSharePoPageInfo.getPages());
        beSharePage.setPageNum(beSharePoPageInfo.getPageNum());
        beSharePage.setPageSize(beSharePoPageInfo.getPageSize());
        beSharePage.setTotal(beSharePoPageInfo.getTotal());

        return new ReturnObject<>(beSharePage);
    }


}
