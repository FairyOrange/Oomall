package cn.edu.xmu.oomall.cart.service.impl;


import cn.edu.xmu.oomall.cart.dao.CartDao;
import cn.edu.xmu.oomall.other.service.ICartService;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ReturnObject;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class CartServiceImpl implements ICartService {


    @Autowired
    private CartDao cartDao;

    @Override
    public ReturnObject<ResponseCode> deleteGoodsInCart(Long customerId, List<Long> skuIdList) {

        ReturnObject returnObject =cartDao.deleteGoodsInCart(customerId,skuIdList);
        return returnObject;
    }
}
