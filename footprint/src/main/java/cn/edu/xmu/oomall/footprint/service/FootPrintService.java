package cn.edu.xmu.oomall.footprint.service;

import cn.edu.xmu.oomall.footprint.FootprintApplication;
import cn.edu.xmu.oomall.footprint.dao.FootPrintDao;
import cn.edu.xmu.oomall.footprint.model.bo.FootPrint;
import cn.edu.xmu.oomall.footprint.model.bo.SkuInfo;
import cn.edu.xmu.oomall.footprint.model.vo.FootPrintRetVo;
import cn.edu.xmu.oomall.goods.model.SkuInfoDTO;
import cn.edu.xmu.oomall.goods.service.IGoodsService;
import cn.edu.xmu.oomall.model.VoObject;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 足迹Service
 *
 * @athor yang8miao
 * @date 2020/11/26 20:18
 * @version 1.0
 */
@Slf4j
@Service
public class FootPrintService {

    @Autowired
    FootPrintDao footPrintDao;

    @DubboReference(check = false)
    private IGoodsService iGoodsService;


    /**
     * 管理员查看浏览记录
     *
     * @param footPrint 前端传递的参数
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页数
     * @param pageSize 每页大小
     * @author yang8miao
     * @date 2020/11/26 20:24
     * @version 1.0
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getFootprints(FootPrint footPrint, String beginTime, String endTime, Integer page, Integer pageSize) {

        ReturnObject<PageInfo<FootPrint>> returnObject =footPrintDao.getFootprints(footPrint, beginTime, endTime, page, pageSize);
        if(returnObject.getCode() != ResponseCode.OK){
            return returnObject;
        }


        log.debug("returnObject.getCode()="+returnObject.getCode());
        log.debug("returnObject.getErrmsg()="+returnObject.getErrmsg());
        log.debug("returnObject.getData()="+returnObject.getData());
        log.debug("returnObject.getData().getList()="+returnObject.getData().getList());

        List<FootPrint> footPrintList = returnObject.getData().getList();

        log.debug("footPrintList.size()="+footPrintList.size());


        List<VoObject> ret = new ArrayList<>(footPrintList.size());

        for(FootPrint bo : footPrintList){


            log.debug("bo.getGoodsSku().getId()="+bo.getGoodsSku().getId());

            // TODO 与商品模块对接，集成时需要解除注释
            ReturnObject<SkuInfoDTO> goodsSku = iGoodsService.getSelectSkuInfoBySkuId(bo.getGoodsSku().getId());

            log.debug("goodsSku.getCode()="+goodsSku.getCode());
            log.debug("goodsSku.getErrmsg()="+goodsSku.getErrmsg());
            log.debug("goodsSku.getData()="+goodsSku.getData());


            SkuInfo goodsSku_new = new SkuInfo();
            goodsSku_new.setId(goodsSku.getData().getId());
            goodsSku_new.setName(goodsSku.getData().getName());
            goodsSku_new.setSkuSn(goodsSku.getData().getSkuSn());
            goodsSku_new.setImageUrl(goodsSku.getData().getImageUrl());
            goodsSku_new.setInventory(goodsSku.getData().getInventory());
            goodsSku_new.setOriginalPrice(goodsSku.getData().getOriginalPrice());
            goodsSku_new.setPrice(goodsSku.getData().getPrice());
            goodsSku_new.setDisable(goodsSku.getData().getDisable() == 1? true : false);
            bo.setGoodsSku(goodsSku_new);

            ret.add(bo);
        }

        PageInfo<VoObject> retObject = new PageInfo<>(ret);

        retObject.setPages(returnObject.getData().getPages());
        retObject.setPageNum(returnObject.getData().getPageNum());
        retObject.setPageSize(returnObject.getData().getPageSize());
        retObject.setTotal(returnObject.getData().getTotal());

        return new ReturnObject<>(retObject);
    }
}
