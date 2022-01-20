package cn.edu.xmu.oomall.footprint.dao;


import cn.edu.xmu.oomall.footprint.mapper.FootPrintPoMapper;
import cn.edu.xmu.oomall.footprint.model.bo.FootPrint;
import cn.edu.xmu.oomall.footprint.model.bo.SkuInfo;
import cn.edu.xmu.oomall.footprint.model.po.FootPrintPo;
import cn.edu.xmu.oomall.footprint.model.po.FootPrintPoExample;
import cn.edu.xmu.oomall.footprint.model.vo.FootPrintRetVo;
import cn.edu.xmu.oomall.footprint.service.FootPrintService;
import cn.edu.xmu.oomall.goods.model.GoodsInfoDTO;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 足迹Dao
 *
 * @author yang8miao
 * @date 2020/11/26 20:21
 * @version 1.0
 */
@Slf4j
@Repository
public class FootPrintDao {

    @Autowired
    private FootPrintPoMapper footPrintPoMapper;

    /**
     * 管理员查看浏览记录
     *
     * @param footPrint 前端传递的参数
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页数
     * @param pageSize 每页大小
     * @author yang8miao
     * @date 2020/11/26 20:28
     * @version 1.0
     */
    public ReturnObject getFootprints(FootPrint footPrint, String beginTime, String endTime, Integer page, Integer pageSize) {

        // 构造查询条件
        FootPrintPoExample footPrintPoExample = new FootPrintPoExample();
        FootPrintPoExample.Criteria footPrintPoCriteria = footPrintPoExample.createCriteria();

        if(footPrint.getCustomerId() != null){
            footPrintPoCriteria.andCustomerIdEqualTo(footPrint.getCustomerId());
        }

        if(beginTime != null){
            footPrintPoCriteria.andGmtCreateGreaterThanOrEqualTo(Timestamp.valueOf(beginTime).toLocalDateTime());
        }

        if(endTime != null){
            footPrintPoCriteria.andGmtCreateLessThanOrEqualTo(Timestamp.valueOf(endTime).toLocalDateTime());
        }

        // 开始时间大于结束时间
        if(beginTime != null && endTime != null){
            if(Timestamp.valueOf(beginTime).toLocalDateTime().isAfter(Timestamp.valueOf(endTime).toLocalDateTime()))
                return new ReturnObject<>(ResponseCode.Log_Bigger);
        }

        try {
            // 根据条件分页查询
            PageHelper.startPage(page,pageSize);
            List<FootPrintPo> footPrintPoList= footPrintPoMapper.selectByExample(footPrintPoExample);

            log.debug("footPrintPoList.size()="+footPrintPoList.size());
            for(FootPrintPo footPrintPo:footPrintPoList)
            {
                log.debug("查询到：footPrintPo.getId()="+footPrintPo.getId());
                log.debug("查询到：footPrintPo.getCustomerId()="+footPrintPo.getCustomerId());
                log.debug("查询到：footPrintPo.getGoodsSkuId()="+footPrintPo.getGoodsSkuId());
                log.debug("查询到：footPrintPo.getGmtCreate()="+footPrintPo.getGmtCreate());
                log.debug("查询到：footPrintPo.getGmtModified()="+footPrintPo.getGmtModified());
            }

            List<FootPrint> ret = new ArrayList<>(footPrintPoList.size());
            for(FootPrintPo po : footPrintPoList){
                FootPrint bo = new FootPrint(po);

                SkuInfo goodsSku_new = new SkuInfo();
                goodsSku_new.setId(po.getGoodsSkuId());
                bo.setGoodsSku(goodsSku_new);

                ret.add(bo);
            }

            PageInfo<FootPrintPo> footPrintPoPageInfo = PageInfo.of(footPrintPoList);
            PageInfo<FootPrint> retObject = new PageInfo<FootPrint>(ret);
            retObject.setPages(footPrintPoPageInfo.getPages());
            retObject.setPageNum(footPrintPoPageInfo.getPageNum());
            retObject.setPageSize(footPrintPoPageInfo.getPageSize());
            retObject.setTotal(footPrintPoPageInfo.getTotal());
            return new ReturnObject<>(retObject);
        }
        catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    }


    /**
     * 增加足迹
     *
     * @param footPrint 前端传递的参数
     * @author yang8miao
     * @date 2020/11/28 16:42
     * @version 1.0
     */
    public ReturnObject<ResponseCode> postUsersIdFootprints(FootPrint footPrint) {

        FootPrintPo po = footPrint.createPo();
        po.setGmtCreate(LocalDateTime.now());
        try {

            // 需要检查 customerId、goodsSpuId在相应表中存不存在(等最后建表)
//            CustomerPo customerPo = CustomerPoMapper.selectByPrimaryKey(footPrint.getCustomerId());
//            if (customerPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }
//            GoodsSpuPo goodsSpuPo = GoodsSpuMapper.selectByPrimaryKey(footPrint.getGoodsSpuId());
//            if (goodsSpuPo == null){
//                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
//            }

            int ret = footPrintPoMapper.insertSelective(po);
            if (ret == 0) {
                // 新建足迹失败
                log.debug("新建足迹失败：" + po.getCustomerId());
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                // 新建足迹成功
                log.debug("新建足迹成功: " + po.toString());
                return new ReturnObject<>(ResponseCode.OK);
            }
        } catch (DataAccessException e) {
            log.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        } catch (Exception e) {
            log.error("其他错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }
}
