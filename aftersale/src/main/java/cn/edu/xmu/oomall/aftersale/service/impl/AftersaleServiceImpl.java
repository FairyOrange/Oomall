package cn.edu.xmu.oomall.aftersale.service.impl;

import cn.edu.xmu.oomall.aftersale.dao.AftersaleDao;
import cn.edu.xmu.oomall.other.model.AftersaleDTO;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class AftersaleServiceImpl implements IAftersaleService {

    @Autowired
    private AftersaleDao aftersaleDao;

    @Override
    public ReturnObject<Long> findUserIdbyAftersaleId(Long aftersaleId) {
        ReturnObject<AftersaleDTO> ret = aftersaleDao.getAftersaleDTOByAftersaleId(aftersaleId);
        if (ret.getCode() == ResponseCode.OK) {
            return new ReturnObject<>(ret.getData().getCustomerId());
        } else {
            return new ReturnObject<>(ret.getCode(), ret.getErrmsg());
        }
    }

    @Override
    public ReturnObject<Long> findShopIdbyAftersaleId(Long aftersaleId) {
        ReturnObject<AftersaleDTO> ret = aftersaleDao.getAftersaleDTOByAftersaleId(aftersaleId);
        if (ret.getCode() == ResponseCode.OK) {
            return new ReturnObject<>(ret.getData().getShopId());
        } else {
            return new ReturnObject<>(ret.getCode(), ret.getErrmsg());
        }
    }

    @Override
    public ReturnObject<Long> findOrderItemIdbyAftersaleId(Long aftersaleId) {
        ReturnObject<AftersaleDTO> ret = aftersaleDao.getAftersaleDTOByAftersaleId(aftersaleId);
        if (ret.getCode() == ResponseCode.OK) {
            return new ReturnObject<>(ret.getData().getOrderItemId());
        } else {
            return new ReturnObject<>(ret.getCode(), ret.getErrmsg());
        }
    }
}
