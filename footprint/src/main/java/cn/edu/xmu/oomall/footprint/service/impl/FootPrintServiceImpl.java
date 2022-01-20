package cn.edu.xmu.oomall.footprint.service.impl;

import cn.edu.xmu.oomall.footprint.dao.FootPrintDao;
import cn.edu.xmu.oomall.footprint.model.bo.FootPrint;
import cn.edu.xmu.oomall.footprint.service.FootPrintService;
import cn.edu.xmu.oomall.other.service.IFootprintService;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;


@DubboService
public class FootPrintServiceImpl implements IFootprintService {

    @Autowired
    private FootPrintDao footPrintDao;

    @Override
    public ReturnObject<ResponseCode> postFootprint(Long customerId, Long skuId) {

        FootPrint footPrint = new FootPrint();
        footPrint.setCustomerId(customerId);
        footPrint.setGoodsSkuId(skuId);
        return footPrintDao.postUsersIdFootprints(footPrint);
    }
}
